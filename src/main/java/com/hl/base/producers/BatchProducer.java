package com.hl.base.producers;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @auth huanglei
 * @date 2022-09-16 9:00
 **/
public class BatchProducer {
    public static void main(String[] args) throws Exception {
        //1.实例化消息生产者producer
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer("base_delay_producer_group");
        //2.设置nameServer的地址
        defaultMQProducer.setNamesrvAddr("192.168.197.3:9876");
        //3.启动producter实例
        defaultMQProducer.start();
        System.out.println("生产者启动");
        List<Message> messages = new ArrayList<>();
        //创建消息的集合
        for (int i = 0; i < 10; i++) {
            String str = "hello world "+i;
            Message message = new Message("base_batch_producer_topic","someTag",str.getBytes());
            messages.add(message);
        }

        defaultMQProducer.send(messages);
        defaultMQProducer.shutdown();
        System.out.println("关闭生产者");
    }
}
