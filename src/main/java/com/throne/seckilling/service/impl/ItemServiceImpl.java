package com.throne.seckilling.service.impl;

import com.throne.seckilling.dao.ItemDOMapper;
import com.throne.seckilling.dao.ItemStockDOMapper;
import com.throne.seckilling.dao.StockLogDOMapper;
import com.throne.seckilling.data_object.ItemDO;
import com.throne.seckilling.data_object.ItemStockDO;
import com.throne.seckilling.data_object.StockLogDO;
import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.error.EnumBusinessError;
import com.throne.seckilling.mq.MqProducer;
import com.throne.seckilling.service.ItemService;
import com.throne.seckilling.service.PromoService;
import com.throne.seckilling.service.model.ItemModel;
import com.throne.seckilling.service.model.PromoModel;
import com.throne.seckilling.validator.ValidationResult;
import com.throne.seckilling.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ItemDOMapper itemDOMapper;
    @Autowired
    private ItemStockDOMapper itemStockDOMapper;
    @Autowired
    private ValidatorImpl validator;
    @Autowired
    private PromoService promoService;
    @Autowired
    private MqProducer mqProducer;
    @Autowired
    private StockLogDOMapper stockLogDOMapper;


    @Override
    public boolean increaseSalesIncacheById(Integer itemId, Integer amount) {
        redisTemplate.opsForValue().increment("promo_item_sales_" + itemId, amount);
        return true;

    }

    @Override
    public ItemModel getCachedItemById(Integer id) throws BusinessException {
        String itemId = "item_" + id.toString();
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get(itemId);
        if (itemModel == null) {
            itemModel = this.getItemById(id);
            redisTemplate.opsForValue().set(itemId, itemModel);
            redisTemplate.expire(itemId, 5, TimeUnit.MINUTES);
        }
        return itemModel;
    }

    @Override
    public List<ItemModel> listItems() {
        List<ItemDO> itemDOList = itemDOMapper.listAllItems();
        return itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = convertItemModelFromItemDO(itemDO);
            itemModel.setStock(itemStockDO.getStock());
            return itemModel;
        }).collect(Collectors.toList());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    public boolean sendDecreaseMsg(Integer itemId, Integer amount) {
        return mqProducer.asyncDecreaseStock(itemId, amount);
    }

    @Override
    public boolean increaseItemStock(Integer itemId, Integer amount) {
        redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount);
        return true;

    }

    @Override
    public void increaseSalesById(Integer itemId, Integer amount) {
        itemDOMapper.increaseSalesById(itemId, amount);
    }

    @Override
    public void decreaseCommonItemStock(Integer itemId, Integer amount) {
        itemStockDOMapper.decreaseItemStock(itemId, amount);
    }

    @Override
    public boolean decreasePromoItemStock(Integer itemId, Integer amount) {
        long result = redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount * -1);
        if (result > 0) {
            return true;
        } else if (result == 0) {
            /*
            当库存售罄时，在缓存中设置售罄标识，由Controller在每次创建订单前读取，
            如果存在售罄标识则直接抛出异常，从而保证不会初始化库存流水到数据库，
            也不会额外地创建订单消息，浪费系统资源
             */
            redisTemplate.opsForValue().set("item_stock_invalid_" + itemId, true);
            return true;
        } else {
            // 如果扣减库存至负数，则必定要将redis内的数据回滚
            redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount);
            return false;
        }
    }

    @Override
    public String initStockLog(Integer itemId, Integer amount) {
        String logId = UUID.randomUUID().toString().replace("-", "");
        StockLogDO stockLogDO = new StockLogDO();
        stockLogDO.setAmount(amount);
        stockLogDO.setItemId(itemId);
        stockLogDO.setStockLogId(logId);
        stockLogDO.setStatus(1);
        stockLogDOMapper.insertSelective(stockLogDO);
        return logId;
    }

    @Override
    public ItemModel getItemById(Integer id) throws BusinessException {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if (itemDO == null) {
            throw new BusinessException(EnumBusinessError.ITEM_NOT_EXISTS);
        }
        ItemModel itemModel = convertItemModelFromItemDO(itemDO);
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemModel.getId());
        itemModel.setStock(itemStockDO.getStock());

        // 将秒杀活动融入商品service逻辑
        PromoModel promoByItemId = promoService.getPromoByItemId(itemModel.getId());
        if (promoByItemId != null && promoByItemId.getStatus() != 3) {
            itemModel.setPromoModel(promoByItemId);
        }

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
        itemStockDO.setStock(itemModel.getStock());
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
