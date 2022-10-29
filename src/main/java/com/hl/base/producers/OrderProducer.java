package com.hl.base.producers;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 全局顺序
 */
public class OrderProducer {
    public static void main(String[] args) throws Exception {
        //1.实例化消息生产者producer
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer("base_order_producer_group");
        //2.设置nameServer的地址
        defaultMQProducer.setNamesrvAddr("192.168.197.3:9876");

        //3.启动producter实例
        defaultMQProducer.start();


        System.out.println("生产者启动");

        List<OrderStep> orderStepList = OrderStep.buildOrders();

        for (int i = 0; i < orderStepList.size(); i++) {
            String body = orderStepList.get(i).toString();
            Message message = new Message("base_order_producer_topic","someTag",i+"",body.getBytes());


            //5.发送消息
            /**
             * 第一个参数是消息体
             * 第二个参数是消息队列选择器
             * 第三个参数是业务标识
             */
            SendResult sendResult = defaultMQProducer.send(message, new MessageQueueSelector() {
                /**
                 * @param mqs  消息队列
                 * @param msg  消息对象
                 * @param arg  是业务标识 上面传入的
                 * @return  返回要选择的MessageQueue
                 */
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    long orderId = (long)arg;
                    int index = (int)(orderId  % mqs.size());
                    return  mqs.get(index);
                }
            },orderStepList.get(i).getOrderId());
            System.out.println(sendResult);
        }
        defaultMQProducer.shutdown();
        System.out.println("关闭生产者");
    }
}
