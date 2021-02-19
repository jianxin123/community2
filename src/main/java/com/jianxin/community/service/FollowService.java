package com.jianxin.community.service;

import com.jianxin.community.entity.User;
import com.jianxin.community.util.CommunityConstant;
import com.jianxin.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    //一个方法里有两次数据库存储 一个是关注目标 一个是被关注对象粉丝加一 用到事务
    public void follow(int userId, int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                //启用事务
                operations.multi();

                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                return operations.exec();//提交事务
            }
        });
    }



    //取消关注
    public void unfollow(int userId, int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                //启用事务
                operations.multi();

                operations.opsForZSet().remove(followeeKey,entityId);
                operations.opsForZSet().remove(followerKey,userId);

                return operations.exec();//提交事务
            }
        });
    }

    //查询关注的实体的数量
    public long findFolloweeCount(int userId,int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    //查询实体的粉丝数量
    public long findFollowerCount(int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //查询当前用户是否已关注该实体
    public boolean hasFollowed(int userId,int entityType,int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey,entityId) != null;
    }

    //查询某用户关注的人
    public List<Map<String,Object>> findFollowees(int userId,int offset,int limit){
            String followeeKey = RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
            //分数是关注时间 倒序查询 最新关注的在最前面  得到一组关注对象的id 存到set集合里
            Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey,offset,offset + limit - 1);
            if(targetIds == null){
                return null;
            }
            //向页面传的不是id而是整个用户信息 需要装到集合里 发送过去
            List<Map<String,Object>> list = new ArrayList<>();
            for(Integer targetId:targetIds){
                Map<String,Object> map = new HashMap<>();
                User user = userService.findUserById(targetId);
                map.put("user",user);
                Double score = redisTemplate.opsForZSet().score(followeeKey,targetId);
                map.put("followTime",new Date(score.longValue()));
                list.add(map);
            }
            return list;
    }


    //查询某用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId,int offset,int limit){
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        //分数是关注时间 倒序查询 最新关注的在最前面  得到一组关注对象的id 存到set集合里
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey,offset,offset + limit - 1);
        if(targetIds == null){
            return null;
        }
        //向页面传的不是id而是整个用户信息 需要装到集合里 发送过去
        List<Map<String,Object>> list = new ArrayList<>();
        for(Integer targetId:targetIds){
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followerKey,targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}
