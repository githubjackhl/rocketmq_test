package com.hl.base.producers;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;


import java.util.concurrent.TimeUnit;

/**
 * 同步消息
 * 这种是是可靠消性同步发送的方式，使用的比较广泛：
 *    重要消息通知，短信通知
 */
public class SyncProducer {
    public static void main(String[] args) throws Exception {
        //1.实例化消息生产者producer
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer("base_sync_producer_group");
        //2.设置nameServer的地址
        defaultMQProducer.setNamesrvAddr("192.168.197.3:9876");
        //3.启动producter实例
        defaultMQProducer.start();
        System.out.println("生产者启动");
        for (int i = 0; i < 10; i++) {
            //4.创建消息对象，主题，Tag和消息体
            String str = "hello world "+i;
            Message message = new Message("base_sync_producer_topic","someTag",str.getBytes());
            //5.发送消息
            SendResult sendResult = defaultMQProducer.send(message);
            //System.out.println("发送状态"+sendResult.getSendStatus()+",发送消息的messageId"+sendResult.getMsgId()+"，接收队列id"+sendResult.getMessageQueue().getQueueId());
            System.out.println(sendResult);
            TimeUnit.SECONDS.sleep(1);
        }
        defaultMQProducer.shutdown();
        System.out.println("关闭生产者");
    }

}
