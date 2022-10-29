package com.hl.base.producers;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

/**
 * 异步发送消息
 * 对响应时间比较敏感业务，即发送端不能容忍长时间的等待Brorker的响应
 * 可靠性不高
 */
public class AsyncProducer {
    public static void main(String[] args) throws Exception {


        //1.实例化消息生产者producer
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer("base_async_producer_group");
        //2.设置nameServer的地址
        defaultMQProducer.setNamesrvAddr("192.168.197.3:9876");
        //3.启动producter实例
        defaultMQProducer.start();
        System.out.println("生产者启动");
        for (int i = 0; i < 10; i++) {
            //4.创建消息对象，主题，Tag和消息体
            String str = "hello world "+i;
            Message message = new Message("base_async_producer_topic","someTag",str.getBytes());
            //5.发送消息
            defaultMQProducer.send(message, new SendCallback() {
                //发送成功的回调
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("发送成功"+sendResult);
                }

                //发送失败的回调
                @Override
                public void onException(Throwable e) {
                    System.out.println("发送失败"+e);
                }
            });
            //System.out.println("发送状态"+sendResult.getSendStatus()+",发送消息的messageId"+sendResult.getMsgId()+"，接收队列id"+sendResult.getMessageQueue().getQueueId());
            TimeUnit.SECONDS.sleep(1);
        }
        defaultMQProducer.shutdown();
        System.out.println("关闭生产者");
    }
}
