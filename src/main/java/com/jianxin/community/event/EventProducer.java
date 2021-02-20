package com.jianxin.community.event;

import com.alibaba.fastjson.JSONObject;
import com.jianxin.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    //生产者需要kafkaTemplate
    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件(发送消息)
    public void fireEvent(Event event){
        //将事件发布到指定的主题   发送的字符串需要包含event的所有信息 可以将event先转为json字符串
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }







}
