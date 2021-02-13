package com.jianxin.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

//request里获取cookie的方法
public class CookieUtil {
    public static String getValue(HttpServletRequest request,String name){
        if(request == null || name == null){
            throw new IllegalArgumentException("参数为空!");
        }

        Cookie[] cookies = request.getCookies();//request.getCookies()得到是所有的cookie 返回的是一个数组
        if(cookies!=null){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
