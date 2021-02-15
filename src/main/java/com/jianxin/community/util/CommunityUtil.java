package com.jianxin.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    //生成随机字符串
    public static String generateUUID(){
        //将生成的随机字符串里的所有-转为空字符串  比如AB-CD -> ABCD
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密  用户提交的密码为明文 容易泄露 需要再次加密
    //MD5只能加密 不能解密 而且每次加密结果都一样 比如密码为hello 每次执行生成的结果都是ABCD 仍然有被盗风险
    //所以需要盐 salt 每次随机生成加在用户密码后面
    public static String md5(String key){
        //如果key为空值
        if(StringUtils.isBlank(key)){
            return null;
        }
        //不为空 转化为16进制  参数为byte 需要将类型为string的key转化一下
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //获取服务器向浏览器发送的json字符串
    //map.keySet() 获得map的key集合
    //map.get(key) 获得键为key的value       代号     消息       具体数据
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null){
            for(String key:map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    //由于传入参数不同 有时没有msg 有时没有map需要重载
    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }
    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }
}
