package com.throne.seckilling.mq;

import com.alibaba.fastjson.JSON;
import com.throne.seckilling.dao.ItemDOMapper;
import com.throne.seckilling.dao.ItemStockDOMapper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * com.throne.seckilling.mq
 * Created by throne on 2020/5/20
 */
@Component
public class MqConsumer {
    private DefaultMQPushConsumer consumer;
    @Value("${mq.name-server.addr}")
    private String nameAddr;

    @Value("${mq.topic-name}")
    private String topicName;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;
    @Autowired
    private ItemDOMapper itemDOMapper;

    @PostConstruct
    public void init() throws MQClientException {
        consumer = new DefaultMQPushConsumer("stock_consumer_group");
        consumer.setNamesrvAddr(nameAddr);
        consumer.subscribe(topicName, "*");

        // 注册listener以指定consumer在监听到消息之后所应采取的行动
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgList, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt msg = msgList.get(0);
                String bodyStr = new String(msg.getBody());
                Map<String, Object> bodyMap = JSON.parseObject(bodyStr, Map.class);
                Integer itemId = (Integer) bodyMap.get("itemId");
                Integer amount = (Integer) bodyMap.get("amount");
                itemStockDOMapper.decreaseItemStock(itemId, amount);
                itemDOMapper.increaseSalesById(itemId, amount);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
    }
}
