package com.jianxin.community.controller.advice;

import com.jianxin.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.PrimitiveIterator;


/**
 * @ControllerAdvice
 *      -用于修饰类 表示该类是Controller的全局配置类
 *      -在此类中，可以对Controller进行如下三种全局配置
 * @ExceptionHandler  异常处理方案
 *      -用于修饰方法 该方法会在Controller出现异常后被调用，用于处理捕获到的异常
 * @ModelAttribute    绑定数据方案
 *      -用于修饰方法 该方法会在Controller方法执行前被调用，用于为Model对象绑定参数
 * @DataBinder
 *      -用于修饰方法 该方法会在Controller方法执行前被调用，用于绑定参数的转换器
 */


//@ControllerAdvice统一处理所有出现在controller里的问题
//annotations = Controller.class 加上后只扫描带有Controller注解的Bean
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final  Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常:" + e.getMessage());
        for(StackTraceElement element:e.getStackTrace()){
            logger.error(element.toString());
        }

        //从request中获取请求方式 异步还是普通的
        String xRequestedWith = request.getHeader("x-requested-with");
        if("XMLHttpRequested".equals(xRequestedWith)){    //返回的是xml格式 属于异步请求
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常！"));
        }else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }


}
