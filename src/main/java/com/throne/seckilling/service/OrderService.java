package com.throne.seckilling.service;

import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.service.model.OrderModel;

public interface OrderService {

    OrderModel createPromoOrder(Integer userId, Integer itemId, Integer amount, Integer promoId, String logId) throws BusinessException;

    OrderModel createCommonOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException;

}
