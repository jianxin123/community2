package com.jianxin.community.controller;


import com.jianxin.community.entity.DiscussPost;
import com.jianxin.community.entity.User;
import com.jianxin.community.service.DiscussPostService;
import com.jianxin.community.service.UserService;
import com.jianxin.community.util.CommunityUtil;
import com.jianxin.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.Dictionary;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

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

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model){
        //得到帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);

        //得到发布帖子的作者  可以查询帖子表的时候关联用户表获取 但是会增加耦合
        //这里采用用userService的方法获取用户 这样会查询两次 影响效率 后期用redis优化 信息存到内存后效率影响就小很多了
        User user = userService.findUserById(post.getuserId());
        model.addAttribute("user",user);

        return "/site/discuss-detail";
    }

}
