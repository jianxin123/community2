package com.jianxin.community.dao;

import com.jianxin.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//一张表一个mapper 注解用于容器自动扫描并实现

@Mapper
public interface DiscussPostMapper {
    //分页查询 因此返回的是一个集合 集合里是DiscussPost对象
    //userId用于将来实现查询自己发布的贴子 平时为0，offset为起始行号，limit每页多少条  orderMode排序模式 默认0按时间排 1是按热度排
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit,int orderMode);

    //用于计算总共有多少条评论 方便显示总页数
    // param注解 sql里需要动态拼接条件即在<if>里使用，因为userId不固定并且这个方法有且只有一个条件，上面那个有三个参数这个参数之前必须取别名
    int selectDiscussPostRows(@Param("userId") int userId);
    //发布帖子
    int insertDiscussPost(DiscussPost discussPost);
    //查看帖子详情
    DiscussPost selectDiscussPostById(int id);

    //添加评论时异步更新帖子评论数量
    int updateCommentCount(int id,int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);

}
