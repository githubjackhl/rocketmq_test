package com.hl.base.producers;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 事务消息
 */
public class TransactionProducer {
    public static void main(String[] args) throws Exception {
        //1.实例化消息生产者producer
        TransactionMQProducer defaultMQProducer = new TransactionMQProducer("base_transaction_producer_group");
        //2.设置nameServer的地址
        defaultMQProducer.setNamesrvAddr("192.168.197.3:9876");
        defaultMQProducer.setTransactionListener(new TransactionListener() {
            /**
             * 处理本地事务
             * @param msg
             * @param arg
             * @return
             */
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                System.out.println("预提交消息成功：" + msg);
                // 假设接收到TAGA的消息就表示扣款操作成功，TAGB的消息表示扣款失败，
                //TAGC表示扣款结果不清楚，需要执行消息回查
                if (StringUtils.equals("TagA", msg.getTags())) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else if (StringUtils.equals("TagB", msg.getTags())) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                } else if (StringUtils.equals("TagC", msg.getTags())) {
                    return LocalTransactionState.UNKNOW;
                }
                return LocalTransactionState.UNKNOW;
            }

            /**
             * 执行回查
             * @param msg
             * @return
             */
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                System.out.println("执行消息回查" + msg.getTags());
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });

        //3.启动producter实例
        defaultMQProducer.start();
        System.out.println("生产者启动");

        String[] strings = new String[]{"TagA","TagB","TagC"};

        for (int i = 0; i < 3; i++) {
            //4.创建消息对象，主题，Tag和消息体
            String str = "hello world "+i;
            Message message = new Message("base_transaction_producer_topic",strings[i%3],str.getBytes());
            //5.发送消息
            SendResult sendResult = defaultMQProducer.sendMessageInTransaction(message,null);
            //System.out.println("发送状态"+sendResult.getSendStatus()+",发送消息的messageId"+sendResult.getMsgId()+"，接收队列id"+sendResult.getMessageQueue().getQueueId());
            System.out.println(sendResult);
            TimeUnit.SECONDS.sleep(1);
        }

        //不要停，否则没有办法回查
        //defaultMQProducer.shutdown();
        //System.out.println("关闭生产者");
    }
}
