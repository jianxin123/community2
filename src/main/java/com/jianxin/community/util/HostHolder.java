package com.jianxin.community.util;

import com.jianxin.community.entity.User;
import org.springframework.stereotype.Component;
/*持有用户信息 用于代替session对象*/
@Component
public class HostHolder {
    //线程隔离
    private  ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
