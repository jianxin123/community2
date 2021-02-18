package com.jianxin.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";//实体赞的前缀

    //获得某个实体的赞
    //格式 like:entity:entityType:entityId    用集合形式set存储 存userId  谁点赞就把id存进去 方便以后看谁点赞了等其他需求
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

}
