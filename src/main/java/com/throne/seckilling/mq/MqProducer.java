package com.throne.seckilling.mq;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
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

    private DefaultMQProducer producer;

    @Value("${mq.name-server.addr}")
    private String nameAddr;

    @Value("${mq.topic-name}")
    private String topicName;


    @PostConstruct
    public void init() throws MQClientException {
        // 初始化MQProducer
        producer = new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr(nameAddr);
        producer.start();
    }

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
}
