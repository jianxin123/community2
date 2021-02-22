package com.jianxin.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {

	//这个注解管理bean的生命周期  主要管理bean的初始化方法
	@PostConstruct
	public void init(){
		//解决netty启动冲突问题 redis 和 es启动都依赖于netty
		//Netty4Utils的setAvailableProcessors()方法
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}



	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}
