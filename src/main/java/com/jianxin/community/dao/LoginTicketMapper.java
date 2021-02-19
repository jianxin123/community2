package com.jianxin.community.dao;

import com.jianxin.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

//这次不采用在resources下新建mapper的方式实现
//采用注解的方式实现sql
//@Deprecated声明这个组件不推荐使用 用redis了
@Mapper
@Deprecated
public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
            })
    @Options(useGeneratedKeys = true,keyProperty = "id")   //声明主键自增 keyProperty声明是loginTicket的哪个属性是主键
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //用户退出不选择删除 选择改变状态
    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateStatus(String ticket,int status);
}
