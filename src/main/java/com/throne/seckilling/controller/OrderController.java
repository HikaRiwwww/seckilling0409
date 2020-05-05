package com.throne.seckilling.controller;


import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.error.EnumBusinessError;
import com.throne.seckilling.response.CommonReturnType;
import com.throne.seckilling.service.OrderService;
import com.throne.seckilling.service.model.OrderModel;
import com.throne.seckilling.service.model.UserModel;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

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
    @RequestMapping("/create_order")
    @ResponseBody
    public CommonReturnType createOrder(
            @RequestParam (name = "item_id") Integer itemId,
            @RequestParam (name = "amount") Integer amount,
            @RequestParam (name = "promoId") Integer promoId
    ) throws BusinessException {
        // 判断用户登录状态
//        HttpSession session = this.request.getSession();
//        Boolean isLogin = (Boolean) session.getAttribute("IS_LOGIN");
//        UserModel userModel = (UserModel) session.getAttribute("LOGIN_USER");
        String uuidToken = request.getParameterMap().get("uuidToken")[0];

        if (StringUtils.isEmpty(uuidToken)){
            throw new BusinessException(EnumBusinessError.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(uuidToken);
        if (userModel == null) {
            throw new BusinessException(EnumBusinessError.USER_NOT_LOGIN);
        }
        Integer userId = userModel.getId();
        OrderModel orderModel = orderService.createOrder(userId, itemId, amount, promoId);
        // 调用service并获取执行结果
        if (orderModel!=null){
            return CommonReturnType.create("success");
        }else {
            return CommonReturnType.create("error", "要不然再试试看？");
        }
    };
}
