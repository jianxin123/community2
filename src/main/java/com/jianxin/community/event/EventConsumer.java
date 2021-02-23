package com.jianxin.community.event;

import com.alibaba.fastjson.JSONObject;
import com.jianxin.community.entity.DiscussPost;
import com.jianxin.community.entity.Event;
import com.jianxin.community.entity.Message;
import com.jianxin.community.service.DiscussPostService;
import com.jianxin.community.service.ElasticsearchService;
import com.jianxin.community.service.MessageService;
import com.jianxin.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger= LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    //三个类型的信息统一处理
    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息内容为空！");
            return;
        }

        //发送者将event转为JSON字符串 消费者恢复成event对象
        //将record.value().toString()得到的字符串解析为Event.class类型的对象
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);

        if(event == null){
            logger.error("消息格式错误！");
            return;
        }

        //发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);//通知是系统用户发送的 指定id为1
        message.setToId(event.getEntityUserId());//通知发送给实体拥有者 比如关注某人 被关注的收到关注通知
        message.setConversationId(event.getTopic());//以前的ConversationId是from_Id 和To_Id拼凑的 现在规定了from_Id是1 就没必要拼凑了 用主题存储
        message.setCreateTime(new Date());

        //这个map用于拼出 用户xxx（userId）评论了你的帖子（entityType，entityId） 点击查看
        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());//存谁触发的 比如是谁点了关注
        content.put("entityType",event.getEntityType());//记录实体类型 比如点赞的是帖子还是评论
        content.put("entityId",event.getEntityId());

        //如果event的content以前就有数据 一起追加进来
        //map.entrySet（）是将map里的每一个键值对取出来封装成一个Entry对象在存到一个Set里面
        //Map.Entry<String, Object>的意思是一个泛型，表示Entry里装的是string字符串和一个Object对象
        if(!event.getData().isEmpty()){
            for(Map.Entry<String,Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }

        //将content转成字符串
        message.setContent(JSONObject.toJSONString(content));
        //存message
        messageService.addMessage(message);
    }


    //消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessages(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event == null){
            logger.error("消息格式错误！");
            return;
        }

        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }



}
