package com.jianxin.community.annotation;
//annotation 存放注解的包
//这个注解用于是否需要在登录状态下才能访问
//@Target(ElementType.METHOD)表示这个注解作用在方法之上
//@Retention(RetentionPolicy.RUNTIME) 声明注解有效时常 运行时有效
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {



}
