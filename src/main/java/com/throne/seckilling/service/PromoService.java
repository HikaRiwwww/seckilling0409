package com.throne.seckilling.service;

import com.throne.seckilling.service.model.PromoModel;

/**
 * 秒杀活动相关的Service
 */
public interface PromoService {
    PromoModel getPromoByItemId(Integer itemId);
}
