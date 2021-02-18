package com.jianxin.community.controller;

import com.jianxin.community.entity.DiscussPost;
import com.jianxin.community.entity.Page;
import com.jianxin.community.entity.User;
import com.jianxin.community.service.DiscussPostService;
import com.jianxin.community.service.LikeService;
import com.jianxin.community.service.UserService;
import com.jianxin.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    //controller查询调用service需要注入
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    //访问的是首页路径为index
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){ //通过model携带数据给模板  页面会传入分页有关的条件 通过page封装
        //方法调用前 springMVC会自动实例化Model和Page 并将Page注入Model
        //所以在thymeleaf中可以直接访问Page对象中的数据 不需要再次model.addattribute.....
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());//返回的对象中包含的是uerId不是用户名需要再处理一下
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for(DiscussPost post : list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);//将查询出来的装到model里 （取名字，取得的集合值）
        return "/index"; //返回的是模板的路径  是templates下的index.html
    }


    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }






}
