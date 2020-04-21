package com.throne.seckilling.service.impl;

import com.throne.seckilling.dao.PromoDOMapper;
import com.throne.seckilling.data_object.PromoDO;
import com.throne.seckilling.service.PromoService;
import com.throne.seckilling.service.model.PromoModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class PromoServiceImpl implements PromoService {
    @Autowired
    private PromoDOMapper promoDOMapper;

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
