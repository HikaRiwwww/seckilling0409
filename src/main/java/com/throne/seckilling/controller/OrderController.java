package com.throne.seckilling.controller;


import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.error.EnumBusinessError;
import com.throne.seckilling.mq.MqProducer;
import com.throne.seckilling.response.CommonReturnType;
import com.throne.seckilling.service.ItemService;
import com.throne.seckilling.service.OrderService;
import com.throne.seckilling.service.PromoService;
import com.throne.seckilling.service.model.UserModel;
import com.throne.seckilling.uitils.VerifyCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 订单相关路由
 */
@Controller
@RequestMapping("/order")
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private ItemService itemService;

    @Autowired
    private PromoService promoService;
    /**
     * 创建订单的路由函数
     * <p>
     * 使用分布式+队列+缓存后的下单流程：
     * 1. 活动商品的相关会通过发布流程注入缓存
     * 2. 下单交易首先在数据库中创建一条库存流水，用于后续追踪
     * 3. 将订单相关信息由producer发送出去
     * 4. producer注册的listener会执行service层面的下单事务，执行成功则发送commit消息
     * 5. consumer注册的listener收到消息后，将之前在缓存中的订单操作同步到数据库层面
     *
     * @param itemId
     * @param amount
     * @param promoId
     * @return
     * @throws BusinessException
     */
    @RequestMapping("/create_order")
    @ResponseBody
    public CommonReturnType createOrder(
            @RequestParam(name = "item_id") Integer itemId,
            @RequestParam(name = "amount") Integer amount,
            @RequestParam(name = "promoId") Integer promoId,
            @RequestParam(name = "wand", defaultValue = "") String wand
    ) throws BusinessException {
        // 判断用户登录状态
        String uuidToken = request.getParameterMap().get("uuidToken")[0];

        if (StringUtils.isEmpty(uuidToken)) {
            throw new BusinessException(EnumBusinessError.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(uuidToken);
        if (userModel == null) {
            throw new BusinessException(EnumBusinessError.USER_NOT_LOGIN);
        }
        Integer userId = userModel.getId();

        /*
        如果没有活动id，则调用普通商品的下单逻辑
        直接请求数据库，不占用秒杀通道
         */
        if (promoId == null) {
            orderService.createCommonOrder(userId, itemId, amount);
            return CommonReturnType.create("success");
        }


        String invalidKey = "item_stock_invalid_" + itemId;
        if (redisTemplate.hasKey(invalidKey)) {
            throw new BusinessException(EnumBusinessError.NOT_ENOUGH_STOCK);
        }

        // 创建订单前先初始化一条库存流水记录以便后续追踪
        String logId = itemService.initStockLog(itemId, amount);
        boolean isOrderCreated = mqProducer.transactionalAsyncDecreaseStock(userId, itemId, amount, promoId, logId);
        // 调用service并获取执行结果
        if (isOrderCreated) {
            return CommonReturnType.create("success");
        } else {
            throw new BusinessException(EnumBusinessError.UNKNOWN_ERROR, "下单失败");
        }
    }


    @RequestMapping(value = "/get_wand", method = RequestMethod.POST)
    @ResponseBody
    public CommonReturnType generateWand(
            @RequestParam(name = "item_id") Integer itemId,
            @RequestParam(name = "promoId") Integer promoId,
            @RequestParam(name = "verifyCode") String verifyCode
    ) throws BusinessException {
        // 判断用户登录状态
        String uuidToken = request.getParameterMap().get("uuidToken")[0];

        if (StringUtils.isEmpty(uuidToken)) {
            throw new BusinessException(EnumBusinessError.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(uuidToken);
        Integer userId = userModel.getId();

        String key = "user_id_" + userId + "_" + "verify_code";
        String codeInRedis = (String) redisTemplate.opsForValue().get(key);
        if (codeInRedis == null || !verifyCode.equals(codeInRedis)){
            throw new BusinessException(EnumBusinessError.USER_PARAM_ERROR, "验证码错误");
        }

        String wand = promoService.generateWand(promoId, userId, itemId);
        if (wand == null){
            throw new BusinessException(EnumBusinessError.USER_PARAM_ERROR, "获取令牌失败");
        }

        return CommonReturnType.create("success", wand);
    }


    @RequestMapping(value = "/get_verify_code", method = RequestMethod.GET)
    @ResponseBody
    public void generateVerifyCode(HttpServletResponse response) throws BusinessException, IOException {
        String uuidToken = request.getParameterMap().get("uuidToken")[0];
        if (StringUtils.isEmpty(uuidToken)) {
            throw new BusinessException(EnumBusinessError.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(uuidToken);
        Integer userId = userModel.getId();
        Map<String, Object> codeMap = VerifyCodeUtil.generateVerifyCode();
        String codes = (String) codeMap.get("codes");
        BufferedImage codeImg = (BufferedImage) codeMap.get("codeImg");

        String key = "user_id_" + userId + "_" + "verify_code";
        redisTemplate.opsForValue().set(key, codes);
        redisTemplate.expire(key, 3, TimeUnit.MINUTES);
        ImageIO.write(codeImg, "jpeg", response.getOutputStream());

    }


}
