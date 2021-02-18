package com.jianxin.community.service;

import com.jianxin.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    //点赞  一个业务中包含两次更新操作 要保证事务性
    //新增entityUserId 参数  如果通过entityId查被赞用户需要访问数据库 redis本身性能优势就没了
    public void like(int userId,int entityType,int entityId,int entityUserId){
//      String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
//      boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);//看当前用户是否已经点过赞了
//      if(isMember){
//          redisTemplate.opsForSet().remove(entityLikeKey,userId);//如果已经点过赞了 再点一次就是取消点赞
//      }else {
//          redisTemplate.opsForSet().add(entityLikeKey,userId);
//      }
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisKeyUtil.geyUserLikeKey(entityUserId);//这里的userId是被赞用户
                boolean isMember = operations.opsForSet().isMember(entityLikeKey,userId);//redis查询操作放在事务外面

                operations.multi();//开启事务

                if(isMember){
                    operations.opsForSet().remove(entityLikeKey,userId);
                    operations.opsForValue().decrement(userLikeKey);//帖子拥有者的被赞数减一
                }else {
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);//帖子拥有者的被赞数加一
                }
                return operations.exec(); //提交事务
            }
        });
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

    //查询某个用户获得的赞
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.geyUserLikeKey(userId);
        Integer count = (Integer)redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }

}
