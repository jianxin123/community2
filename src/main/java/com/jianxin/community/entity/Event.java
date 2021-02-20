package com.jianxin.community.entity;

import java.util.HashMap;
import java.util.Map;


/*发送系统通知
触发事件 ：评论后 点赞后 关注后 发布通知 操作频繁 需要kafka*/

//封装触发事件时相关的一切信息
public class Event {
    private String topic;//事件类型
    private int userId;//触发事件的人
    private int entityType;//触发在什么东西上
    private int EntityId;
    private int EntityUserId;//实体拥有者
    private Map<String,Object> data = new HashMap<>();//内容 相当于content 用于存放将来可能扩展的信息


    public String getTopic() {
        return topic;
    }

    //加了返回值可以xxx.setsetTopic().setxxx 一直点下去
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return EntityId;
    }

    public Event setEntityId(int entityId) {
        EntityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return EntityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        EntityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key,Object value) {
        this.data.put(key,value);
        return this;
    }
}
