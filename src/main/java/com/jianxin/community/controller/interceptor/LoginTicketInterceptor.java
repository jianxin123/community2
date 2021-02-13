package com.jianxin.community.controller.interceptor;

import com.jianxin.community.entity.LoginTicket;
import com.jianxin.community.entity.User;
import com.jianxin.community.service.UserService;
import com.jianxin.community.util.CookieUtil;
import com.jianxin.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    //第一步 通过浏览器发送的cookie得到ticket  而接口规定了参数 不能用注解方式获得cookie 可以从request里获取   需要封装一下request里获取cookie的方法

    //需要用到UserService里的方法 先注入
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取凭证
        String ticket = CookieUtil.getValue(request,"ticket");
        if(ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效                                                      凭证过期时间晚于当前时间
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //凭证有效后 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }
        return true;
    }
    //模板调用之前 将user注到model里
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
