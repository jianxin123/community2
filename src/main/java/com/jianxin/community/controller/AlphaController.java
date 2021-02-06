package com.jianxin.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//controller处理请求->访问业务层service->访问数据库dao->DB
//先定义实体类entity 各个成员属性 get set tostring
// 然后到dao写mapper接口
//然后到resources里写mapper.xml实现
//然后转到业务层service
//然后基于service写视图层 controller
//最后转到html
//debug   f8从断点处逐行运行   f7跳到当前行的内部调用的方法  f9 跳到下一个断点处

@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot";
    }
}
