package com.throne.seckilling.service.impl;

import com.throne.seckilling.dao.PromoDOMapper;
import com.throne.seckilling.data_object.PromoDO;
import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.error.EnumBusinessError;
import com.throne.seckilling.service.ItemService;
import com.throne.seckilling.service.PromoService;
import com.throne.seckilling.service.UserService;
import com.throne.seckilling.service.model.ItemModel;
import com.throne.seckilling.service.model.PromoModel;
import com.throne.seckilling.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PromoServiceImpl implements PromoService {
    @Autowired
    private PromoDOMapper promoDOMapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        PromoDO promoDO = promoDOMapper.getPromoByItemId(itemId);
        if (promoDO == null) {
            return null;
        }
        PromoModel promoModel = convertPromoDOToModel(promoDO);

        // 根据时间判断并设置活动状态
        Date now = new Date();
        if (now.before(promoModel.getStartDate())) {
            promoModel.setStatus(1);
        } else if (now.after(promoModel.getEndDate())) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        }

        return promoModel;
    }

    /**
     * 用来处理活动发布的service，发布后，活动商品的相关信息将从数据库中被同步至service
     *
     * @param promoId
     */
    @Override
    public void publishPromo(Integer promoId) throws BusinessException {
        // 确认活动商品存在
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        if (promoDO.getItemId() == null || promoDO.getItemId() == 0) {
            throw new BusinessException(EnumBusinessError.ITEM_NOT_EXISTS);
        }
        // 将商品信息同步至缓存: 库存和销量
        ItemModel itemModel = itemService.getItemById(promoDO.getItemId());
        redisTemplate.opsForValue().set("promo_item_stock_" + itemModel.getId(), itemModel.getStock());
        redisTemplate.opsForValue().set("promo_item_sales_" + itemModel.getId(), itemModel.getSales());

    }


    @Override
    public String generateWand(Integer promoId, Integer userId, Integer itemId) {
        // 校验用户和商品信息
        ItemModel itemById = null;
        try {
            itemById = itemService.getCachedItemById(itemId);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        if (itemById == null) {
            return null;
        }
        UserModel userById = userService.getCachedUserById(userId);
        if (userById == null) {
            return null;
        }
        // 校验活动信息
        if (promoId != null) {
            if (!promoId.equals(itemById.getPromoModel().getId())) {
                return null;
            } else if (itemById.getPromoModel().getStatus() != 2) {
                return null;
            }
        }
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        if (promoDO == null) {
            return null;
        }
        PromoModel promoModel = convertPromoDOToModel(promoDO);

        // 根据时间判断并设置活动状态
        Date now = new Date();
        if (now.before(promoModel.getStartDate())) {
            promoModel.setStatus(1);
        } else if (now.after(promoModel.getEndDate())) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        }
        if (promoModel.getStatus() != 2) {
            return null;
        }

        String wand = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("promo_token_" + promoId, wand);
        redisTemplate.expire("promo_token_" + promoId, 5, TimeUnit.MINUTES);

        return wand;

    }

    public PromoModel convertPromoDOToModel(PromoDO promoDO) {
        if (promoDO == null) {
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO, promoModel);
        promoModel.setSecPrice(new BigDecimal(promoDO.getSecPrice()));
        return promoModel;
    }

}
