package com.jianxin.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";//实体赞的前缀
    private static final String PREFIX_USER_LIKE = "like:user";   //用户获得赞的前缀
    private static final String PREFIX_FOLLOWEE = "followee";   //被关注对象
    private static final String PREFIX_FOLLOWER = "follower";   //关注人
    private static final String PREFIX_KAPTCHA = "kaptcha"; //存验证码
    private static final String PREFIX_TICKET = "ticket"; //登录凭证
    private static final String PREFIX_USER = "user";  //用户信息缓存
    private static final String PREFIX_UV = "uv"; //独立访客
    private static final String PREFIX_DAU = "dau";//日活跃用户

    //获得某个实体的赞的key
    //格式 like:entity:entityType:entityId    用集合形式set存储 存userId  谁点赞就把id存进去 方便以后看谁点赞了等其他需求
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //某个用户获得的赞的key
    //格式like:user:userId  -> int
    public static String geyUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //某个用户关注的实体
    //key为followee:userId:entityType 包含了具体用户 关注对象类型 值为entityId 具体对象Id 以zset类型存储 分数为当前时间
    //followee:userId:entityType -> zset(entityId,now）
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //某个实体拥有的粉丝
    //follower:entityType:entityId -> zset(userId,now）
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER + SPLIT +entityType + SPLIT + entityId;
    }


    //登录验证码的key
    //用户访问登录页面时给用户发送一个凭证（随机生成的字符串） 让用户存到cookie里  以字符串标识用户 让字符串很快过期  因为用户没登陆得不到userId
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //登录凭证key
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    //用户
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

    //单日UV
    public static String getUVkey(String date){
        return PREFIX_UV + SPLIT + date;
    }

    //区间UV 获得一周内的uv需要令设置一个key存储
    public static String getUVkey(String startDate,String endDate){
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    //单日活跃用户
    public static String getDAUkey(String date){
        return PREFIX_DAU + SPLIT + date;
    }

    //区间活跃用户
    public static String getDAUkey(String startDate,String endDate){
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }
}
