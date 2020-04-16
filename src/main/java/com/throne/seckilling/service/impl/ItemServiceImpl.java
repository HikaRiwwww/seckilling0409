package com.throne.seckilling.service.impl;

import com.throne.seckilling.dao.ItemDOMapper;
import com.throne.seckilling.dao.ItemStockDOMapper;
import com.throne.seckilling.data_object.ItemDO;
import com.throne.seckilling.data_object.ItemStockDO;
import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.error.EnumBusinessError;
import com.throne.seckilling.service.ItemService;
import com.throne.seckilling.service.model.ItemModel;
import com.throne.seckilling.validator.ValidationResult;
import com.throne.seckilling.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDOMapper itemDOMapper;
    @Autowired
    private ItemStockDOMapper itemStockDOMapper;
    @Autowired
    private ValidatorImpl validator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ItemModel> listItems() {

        return null;
    }

    @Override
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        // 校验入参
        ValidationResult result = validator.validateBean(itemModel);
        if (result.isError()) {
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrorMsg());
        }
        // 对象转换
        ItemDO itemDO = convertItemDOFromModel(itemModel);
        // 写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());
        ItemStockDO itemStockDO = convertItemStockDOFromModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);
        // 返回对象
        return this.getItemById(itemDO.getId());
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        ItemModel itemModel = convertItemModelFromItemDO(itemDO);
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemModel.getId());
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }

    public ItemDO convertItemDOFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }

    public ItemStockDO convertItemStockDOFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getStock());
        itemStockDO.setItemId(itemModel.getId());
        return itemStockDO;
    }

    public ItemModel convertItemModelFromItemDO(ItemDO itemDO) {
        if (itemDO == null) {
            return null;
        }
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        return itemModel;
    }


}
