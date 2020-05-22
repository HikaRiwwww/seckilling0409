package com.throne.seckilling.service.impl;

import com.throne.seckilling.dao.OrderDOMapper;
import com.throne.seckilling.dao.SequenceDOMapper;
import com.throne.seckilling.dao.StockLogDOMapper;
import com.throne.seckilling.data_object.OrderDO;
import com.throne.seckilling.data_object.SequenceDO;
import com.throne.seckilling.data_object.StockLogDO;
import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.error.EnumBusinessError;
import com.throne.seckilling.service.ItemService;
import com.throne.seckilling.service.OrderService;
import com.throne.seckilling.service.UserService;
import com.throne.seckilling.service.model.ItemModel;
import com.throne.seckilling.service.model.OrderModel;
import com.throne.seckilling.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderDOMapper orderDOMapper;
    @Autowired
    private SequenceDOMapper sequenceDOMapper;
    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderModel createOrder(Integer userId, Integer itemId, Integer amount, Integer promoId, String logId) throws BusinessException {
        // 校验下单状态
        ItemModel itemById = itemService.getCachedItemById(itemId);
        if (itemById == null) {
            throw new BusinessException(EnumBusinessError.ITEM_NOT_EXISTS);
        }
        UserModel userById = userService.getCachedUserById(userId);
        if (userById == null) {
            throw new BusinessException(EnumBusinessError.USER_NOT_EXISTS);
        }
        // 校验活动信息
        if (promoId != null) {
            if (!promoId.equals(itemById.getPromoModel().getId())) {
                throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息不存在");
            } else if (itemById.getPromoModel().getStatus() != 2) {
                throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "活动尚未开始");
            }

        }

        //落单减库存  这里扣减的是redis中缓存的活动商品库存，此时，消息还未被同步给数据库
        boolean isDecreased = itemService.decreaseItemStock(itemId, amount);
        if (!isDecreased) {
            throw new BusinessException(EnumBusinessError.NOT_ENOUGH_STOCK);
        }
        // 增加销量 也先在redis中进行扣减，再发送异步消息让数据库同步数据进行管理
        boolean isIncreased = itemService.increaseSalesById(itemId, amount);
        if (!isIncreased){
            throw new BusinessException(EnumBusinessError.UNKNOWN_ERROR);
        }


        //订单入库
        OrderModel orderModel = new OrderModel();
        String orderNum = generateOrderNum();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setId(orderNum);
        orderModel.setAmount(amount);
        if (promoId != null) {
            orderModel.setItemPrice(itemById.getPromoModel().getSecPrice());
            orderModel.setPromoId(promoId);
        } else {
            orderModel.setItemPrice(itemById.getPrice());
        }
        orderModel.setTotalPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));
        OrderDO orderDO = convertOrderModelToOderDO(orderModel);
        orderDOMapper.insertSelective(orderDO);


        /*
            设置库存流水状态为创建成功
            由于创建订单和设置库存流水在同一事务，
            不论哪一部分操作失败都会rollback，
            因此不会出现订单创建成功库存流水却设置失败的情况
       */
        StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(logId);
        if (stockLogDO == null) {
            throw new BusinessException(EnumBusinessError.UNKNOWN_ERROR, "库存流水不存在，订单创建失败");
        }
        stockLogDO.setStatus(2);
        stockLogDOMapper.updateByPrimaryKey(stockLogDO);

        return orderModel;
    }

    private OrderDO convertOrderModelToOderDO(OrderModel orderModel) {
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setTotalPrice(orderModel.getTotalPrice().doubleValue());
        return orderDO;
    }

    /**
     * 生成订单号  日期字符串+自增数字+分库分表位
     * todo: 分库分表位暂时写死为“00”
     *
     * @return 订单号
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderNum() {
        StringBuilder builder = new StringBuilder();
        LocalDateTime localDateTime = LocalDateTime.now();
        String dateStr = localDateTime.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        builder.append(dateStr);

        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");

        int currentVal = sequenceDO.getCurrentVal() + sequenceDO.getStep();

        if (currentVal >= sequenceDO.getMaxValue()) {
            sequenceDO.setCurrentVal(sequenceDO.getInitValue());
        } else {
            sequenceDO.setCurrentVal(currentVal);
        }
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        for (int i = 0; i < 6 - String.valueOf(currentVal).length(); i++) {
            builder.append("0");
        }
        builder.append(currentVal);
        builder.append("00");
        return builder.toString();
    }


}
