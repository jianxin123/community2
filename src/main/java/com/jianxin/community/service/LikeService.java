package com.jianxin.community.service;

import com.jianxin.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    //点赞
    public void like(int userId,int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);//看当前用户是否已经点过赞了
        if(isMember){
            redisTemplate.opsForSet().remove(entityLikeKey,userId);//如果已经点过赞了 再点一次就是取消点赞
        }else {
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }
    }

    //查询某实体点赞数量
    public long findEntityLikeCount(int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某人对某实体的点赞状态  不用布尔值 将来可能会有踩 就会有三种状态
    public int findEntityLikeStatus(int userId,int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0;
    }


}
