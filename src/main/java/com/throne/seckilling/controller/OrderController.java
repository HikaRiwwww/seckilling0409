package com.throne.seckilling.controller;


import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.error.EnumBusinessError;
import com.throne.seckilling.response.CommonReturnType;
import com.throne.seckilling.service.OrderService;
import com.throne.seckilling.service.model.OrderModel;
import com.throne.seckilling.service.model.UserModel;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

    @RequestMapping("/create_order")
    @ResponseBody
    public CommonReturnType createOrder(
            @RequestParam (name = "item_id") Integer itemId,
            @RequestParam (name = "amount") Integer amount,
            @RequestParam (name = "promoId") Integer promoId
    ) throws BusinessException {
        // 判断用户登录状态
        HttpSession session = this.request.getSession();
        Boolean isLogin = (Boolean) session.getAttribute("IS_LOGIN");
        UserModel userModel = (UserModel) session.getAttribute("LOGIN_USER");
        if (isLogin==null || !isLogin){
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
