package com.jianxin.community.controller.interceptor;

import com.jianxin.community.annotation.LoginRequired;
import com.jianxin.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

//添加注解后需要拦截器处理
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //首先判断拦截下来的东西是不是一个方法  是就处理 因为拦截可能拦截下来的是静态资源或其它内容 而我们只判定方法
        //spring自带的判断handler是不是一个方法 如果是就是HandlerMethod类型
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;  //将原来的Object类型转为HandlerMethod类型
            Method method = handlerMethod.getMethod();              //取其中的方法
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class); //获得想要的注解
            // loginRequired != null即需要登录才能访问  hostHolder.getUser()==null即没登陆
            if(loginRequired != null && hostHolder.getUser()==null){
                //由于接口返回类型定死了 不能返回到指定页面  用response重定向
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }

        }
        return true;
    }
}
