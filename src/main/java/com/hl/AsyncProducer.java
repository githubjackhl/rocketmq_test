package com.hl;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

/**
 * 异步发送
 */
public class AsyncProducer {
    public static void main(String[] args) throws Exception {
        // 创建一个producer，参数为Producer Group名称
        DefaultMQProducer producer = new DefaultMQProducer("b_producer_group");
        // 指定nameServer地址
        producer.setNamesrvAddr("192.168.197.3:9876");
        // 设置当发送失败时重试发送的次数，默认为2次
        producer.setRetryTimesWhenSendAsyncFailed(0);
        // 设置发送超时时限为5s，默认3s
        producer.setSendMsgTimeout(5000);
        producer.setDefaultTopicQueueNums(8);
        // 开启生产者
        producer.start();
        // 生产并发送100条消息
        for (int i = 0; i < 100; i++) {
            byte[] body = ("Hi," + i).getBytes();
            Message msg = new Message("my_topic_async", "SomeTopic", body);
            // 消息指定key
            msg.setKeys("key-" + i);
            //  异步发送。指定回调
            producer.send(msg, new SendCallback() {
                @Override
                //当producer接收到MQ发送来的ACK后就会触发该回调方法的执行
                public void onSuccess(SendResult sendResult) {
                    System.out.println(sendResult);
                }


                @Override
                public void onException(Throwable e) {
                    e.printStackTrace();
                }
            });


        }
        // sleep一会儿
        // 由于采用的是异步发送，所以若这里不sleep，
        // 则消息还未发送就会将producer给关闭，报错
        TimeUnit.SECONDS.sleep(3);
        // 关闭producer
        producer.shutdown();
    }
}
