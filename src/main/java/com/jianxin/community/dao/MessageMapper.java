package com.jianxin.community.dao;

import com.jianxin.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户的会话列表 针对每个会话只返回一条最新的数据
    List<Message> selectConversations(int userId,int offset,int limit);

    //查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId,int offset,int limit);

    //查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读私信数量
    int selectLetterUnreadCount(int userId,String conversationId);

    //发送私信(异步)
    int insertMessage(Message message);

    //修改信息状态 未读到已读 或删除
    int updateStatus(List<Integer> ids,int status);
}
