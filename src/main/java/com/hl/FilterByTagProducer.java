package com.hl;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * @auth huanglei
 * @date 2022-01-17 21:35
 **/
public class FilterByTagProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("filter_producer_group");
        producer.setNamesrvAddr("192.168.197.3:9876");
        producer.start();
        //均包含下面Tag，为下面3种，生成了下面3种
        String[] tags = {"myTagA","myTagB","myTagC"};
        for (int i = 0; i < 10; i++) {
            byte[] body = ("Hi," + i).getBytes();
            String tag =  tags[i%tags.length];
            Message msg = new Message("filterTopicA",tag,body);
            SendResult sendResult = producer.send(msg);
            System.out.println(sendResult);
        }
        producer.shutdown();
    }
}
