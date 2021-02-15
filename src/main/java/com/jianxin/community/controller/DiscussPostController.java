package com.jianxin.community.controller;


import com.jianxin.community.entity.DiscussPost;
import com.jianxin.community.entity.User;
import com.jianxin.community.service.DiscussPostService;
import com.jianxin.community.util.CommunityUtil;
import com.jianxin.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    //页面中只需传入title content两个参数
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if(user == null){  //即还没有登陆
            return CommunityUtil.getJSONString(403,"你还没有登录！");  //异步发送  403表示权限不够
        }
        DiscussPost post = new DiscussPost();
        post.setuserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        //报错的情况将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功！");  //0表示正确的状态
    }

}
