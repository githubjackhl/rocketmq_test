package com.hl.base.consumers;


import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

public class OrderConsumer {
    public static void main(String[] args) throws Exception {
        //定义一个pull消费者
        //DefaultLitePullConsumer consumer = new DefaultLitePullConsumer("consumer_group");
        // 定义一个push消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("base_order_producer_topic");

        // 指定nameServer
        consumer.setNamesrvAddr("192.168.197.3:9876");

        // 指定从第一条消息开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        //默认是集群模式
        consumer.setMessageModel(MessageModel.CLUSTERING);


        // 指定消费topic与tag
        consumer.subscribe("base_order_producer_topic", "someTag");

        // 注册消息监听器,使用MessageListenerOrderly,那么就会使用一个线程来消费
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                for (MessageExt msg : msgs) {
                    System.out.println(new String(msg.getBody()));
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });


        // 开启消费者消费
        consumer.start();
        System.out.println("启动消费者");
    }
}
