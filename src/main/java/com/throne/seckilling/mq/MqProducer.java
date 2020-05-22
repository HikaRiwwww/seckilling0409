package com.throne.seckilling.mq;

import com.alibaba.fastjson.JSON;
import com.throne.seckilling.dao.StockLogDOMapper;
import com.throne.seckilling.data_object.StockLogDO;
import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.service.OrderService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * com.throne.seckilling.mq
 * Created by throne on 2020/5/20
 */
@Component
public class MqProducer {

    @Value("${mq.name-server.addr}")
    private String nameAddr;

    @Value("${mq.topic-name}")
    private String topicName;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    private TransactionMQProducer producer;


    @PostConstruct
    public void init() throws MQClientException {
        // 初始化MQProducer
        producer = new TransactionMQProducer("producer_group");
        producer.setNamesrvAddr(nameAddr);
        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object args) {
                Map<String, Object> argsMap = (Map<String, Object>) args;
                Integer itemId = (Integer) argsMap.get("itemId");
                Integer amount = (Integer) argsMap.get("amount");
                Integer userId = (Integer) argsMap.get("userId");
                Integer promoId = (Integer) argsMap.get("promoId");
                String logId = (String) argsMap.get("logId");
                try {
                    orderService.createOrder(userId, itemId, amount, promoId, logId);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            /*
            在事务状态长时间未反馈或者事务执行过程异常中断时，主动获取事务状态的方法
            而要知道一笔订单事务仅靠itemId和amount是不够的，因此需要引入订单流水的概念作为判断依据
            即：需要知道当前处理的事务到底是哪一笔交易，根据其实际状态对其进采取正确的处理方式
             */
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {

                String bodyStr = new String(messageExt.getBody());
                Map<String, Object> bodyMap = JSON.parseObject(bodyStr, Map.class);

                // todo:这两个参数有没有用？
                Integer itemId = (Integer) bodyMap.get("itemId");
                Integer amount = (Integer) bodyMap.get("amount");

                String logId = (String) bodyMap.get("logId");
                StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(logId);
                if (stockLogDO == null) {
                    return LocalTransactionState.UNKNOW;
                }
                Integer status = stockLogDO.getStatus();
                if (status == 2) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else if (status == 1) {
                    return LocalTransactionState.UNKNOW;
                } else {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }

            }
        });
        producer.start();
    }

    @Deprecated
    /**
     * 生产者发送同步商品库存信息的方法
     * @param itemId 商品id
     * @param amount 商品增加的数量 扣减为负
     * @return 消息发送是否成功，是为true
     */
    public boolean asyncDecreaseStock(Integer itemId, Integer amount) {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("amount", amount);
        Message message = new Message(topicName, "increase", JSON.toJSONBytes(map));
        try {
            producer.send(message);
        } catch (MQClientException | MQBrokerException | RemotingException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean transactionalAsyncDecreaseStock(Integer userId, Integer itemId, Integer amount, Integer promoId, String logId) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId", itemId);
        bodyMap.put("amount", amount);
        bodyMap.put("logId", logId);

        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("itemId", itemId);
        argsMap.put("amount", amount);
        argsMap.put("userId", userId);
        argsMap.put("promoId", promoId);
        argsMap.put("logId", logId);


        Message message = new Message(topicName, "increase", JSON.toJSONBytes(bodyMap));
        try {
            TransactionSendResult result = producer.sendMessageInTransaction(message, argsMap);
            LocalTransactionState state = result.getLocalTransactionState();
            return state == LocalTransactionState.COMMIT_MESSAGE;
        } catch (MQClientException e) {
            // 如果出现异常，要讲stockLog设置为需要回滚
            StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(logId);
            stockLogDO.setStatus(3);
            stockLogDOMapper.insertSelective(stockLogDO);
            e.printStackTrace();
            return false;
        }
    }
}
