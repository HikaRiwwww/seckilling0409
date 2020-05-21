package com.throne.seckilling.controller;


import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.error.EnumBusinessError;
import com.throne.seckilling.mq.MqProducer;
import com.throne.seckilling.response.CommonReturnType;
import com.throne.seckilling.service.OrderService;
import com.throne.seckilling.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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


    @RequestMapping("/create_order")
    @ResponseBody
    public CommonReturnType createOrder(
            @RequestParam(name = "item_id") Integer itemId,
            @RequestParam(name = "amount") Integer amount,
            @RequestParam(name = "promoId") Integer promoId
    ) throws BusinessException {
        // 判断用户登录状态
        String uuidToken = request.getParameterMap().get("uuidToken")[0];

        if (StringUtils.isEmpty(uuidToken)){
            throw new BusinessException(EnumBusinessError.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(uuidToken);
        if (userModel == null) {
            throw new BusinessException(EnumBusinessError.USER_NOT_LOGIN);
        }
        Integer userId = userModel.getId();
        boolean isOrderCreated = mqProducer.transactionalAsyncDecreaseStock(userId, itemId, amount, promoId);
        // 调用service并获取执行结果
        if (isOrderCreated) {
            return CommonReturnType.create("success");
        } else {
            throw new BusinessException(EnumBusinessError.UNKNOWN_ERROR, "下单失败");
        }
    };
}
