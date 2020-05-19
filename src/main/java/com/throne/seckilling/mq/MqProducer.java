package com.throne.seckilling.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
}
