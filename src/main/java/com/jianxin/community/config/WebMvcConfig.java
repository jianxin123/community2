package com.jianxin.community.config;


import com.jianxin.community.controller.interceptor.AlphaInterceptor;
import com.jianxin.community.controller.interceptor.LoginRequiredInterceptor;
import com.jianxin.community.controller.interceptor.LoginTicketInterceptor;
import com.jianxin.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//一般来说config是用来配置第三方的BEAN
//拦截器需要实现一个接口 不仅仅是bean
//应用 1在请求开始时查询登录用户 2在本次请求中持有用户数据 （存在内存里）3在模板视图上显示用户数据 4在请求结束时清理用户数据
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //配拦截器 先把拦截器注入进来
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

//    @Autowired
//    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    //默认拦截一切路径  //排除所有css等文件 静态资源随便访问  //添加需要拦截的路径  现在只拦截这两个路径
    //貌似是版本问题只能用/*/*.css  不能用/**/*.css
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg")
                .addPathPatterns("/register","/login");

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg");

//        registry.addInterceptor(loginRequiredInterceptor)
//                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg");
    }
}
