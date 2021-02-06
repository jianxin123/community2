package com.jianxin.community.dao;

import com.jianxin.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
//mapper只需要写接口  在xml里做映射  xml放在resources里
@Mapper
public interface UserMapper {
    User selectById(int id);
    User selectByName(String username);
    User selectByEmail(String email);
    int insertUser(User user);
    int updateStatus(int id, int status);
    int updateHeader(int id, String headerUrl);
    int updatePassword(int id, String password);
}
