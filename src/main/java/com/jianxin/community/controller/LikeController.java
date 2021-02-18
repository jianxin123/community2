package com.jianxin.community.controller;

import com.jianxin.community.entity.User;
import com.jianxin.community.service.LikeService;
import com.jianxin.community.util.CommunityUtil;
import com.jianxin.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    // @ResponseBody用于异步请求
    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId){
        User user = hostHolder.getUser();

        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);

        //获得该实体点赞数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //获得当前用户点赞状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType,entityId);
        //向页面传回的结果
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        return CommunityUtil.getJSONString(0,null,map);//0代表正常
    }

}
