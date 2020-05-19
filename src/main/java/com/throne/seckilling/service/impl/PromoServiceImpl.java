package com.throne.seckilling.service.impl;

import com.throne.seckilling.dao.PromoDOMapper;
import com.throne.seckilling.data_object.PromoDO;
import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.error.EnumBusinessError;
import com.throne.seckilling.service.ItemService;
import com.throne.seckilling.service.PromoService;
import com.throne.seckilling.service.model.ItemModel;
import com.throne.seckilling.service.model.PromoModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class PromoServiceImpl implements PromoService {
    @Autowired
    private PromoDOMapper promoDOMapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        PromoDO promoDO = promoDOMapper.getPromoByItemId(itemId);
        if (promoDO == null){
            return null;
        }
        PromoModel promoModel = convertPromoDOToModel(promoDO);

        // 根据时间判断并设置活动状态
        Date now = new Date();
        if (now.before(promoModel.getStartDate())){
            promoModel.setStatus(1);
        }else if (now.after(promoModel.getEndDate())){
            promoModel.setStatus(3);
        }else {
            promoModel.setStatus(2);
        }

        return promoModel;
    }

    /**
     * 用来处理活动发布的service，发布后，活动商品的相关信息将从数据库中被同步至service
     * @param promoId
     */
    @Override
    public void publishPromo(Integer promoId) throws BusinessException {
        // 确认活动商品存在
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        if (promoDO.getItemId() == null || promoDO.getItemId() == 0) {
            throw new BusinessException(EnumBusinessError.ITEM_NOT_EXISTS);
        }
        // 将商品信息同步至缓存
        ItemModel itemModel = itemService.getItemById(promoDO.getId());
        redisTemplate.opsForValue().set("promo_item_stock_" + itemModel.getId(), itemModel.getStock());

    }


    public PromoModel convertPromoDOToModel(PromoDO promoDO) {
        if (promoDO == null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO, promoModel);
        promoModel.setSecPrice(new BigDecimal(promoDO.getSecPrice()));
        return promoModel;
    }

}
