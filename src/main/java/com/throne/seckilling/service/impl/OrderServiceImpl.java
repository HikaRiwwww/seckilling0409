package com.throne.seckilling.service.impl;

import com.throne.seckilling.dao.OrderDOMapper;
import com.throne.seckilling.dao.SequenceDOMapper;
import com.throne.seckilling.data_object.OrderDO;
import com.throne.seckilling.data_object.SequenceDO;
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


    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException {
        // 校验下单状态
        ItemModel itemById = itemService.getItemById(itemId);
        if (itemById == null) {
            throw new BusinessException(EnumBusinessError.ITEM_NOT_EXISTS);
        }
        UserModel userById = userService.getUserById(userId);
        if (userById == null) {
            throw new BusinessException(EnumBusinessError.USER_NOT_EXISTS);
        }
        //落单减库存
        boolean isDecreased = itemService.decreaseItemStock(itemId, amount);
        if (!isDecreased) {
            throw new BusinessException(EnumBusinessError.NOT_ENOUGH_STOCK);
        }
        // 增加销量
        itemService.increaseSalesById(itemId, amount);

        //订单入库
        OrderModel orderModel = new OrderModel();
        String orderNum = generateOrderNum();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setId(orderNum);
        orderModel.setAmount(amount);
        orderModel.setItemPrice(itemById.getPrice());
        orderModel.setTotalPrice(itemById.getPrice().multiply(new BigDecimal(amount)));
        OrderDO orderDO = convertOrderModelToOderDO(orderModel);
        orderDOMapper.insertSelective(orderDO);

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
        }else {
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

    ;

}
