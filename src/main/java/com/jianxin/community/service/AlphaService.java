package com.jianxin.community.service;

import com.jianxin.community.dao.DiscussPostMapper;
import com.jianxin.community.dao.UserMapper;
import com.jianxin.community.entity.DiscussPost;
import com.jianxin.community.entity.User;
import com.jianxin.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
public class AlphaService {

    /**
     * 模拟事务管理
     * 新增用户时 添加新人报到帖子 将2个操作并到一个事务
     */
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    //添加注解后 这个方法就会做事务管理 里面任何地方有错误都会回滚 即新增用户和帖子提交不上去
    //默认会自动帮你选择隔离级别 手动选择则isolation = Isolation.
    //propagation传播机制： 解决事务A调用事务B 事务交叉怎么抉择
    //     REQUIRED：支持当前事务，如果不存在则创建新事务 A调B  A有事务就按A的来 A没有事务B就新建一个事务
    //     REQUIRES_NEW：创建一个新事务，并且暂停当前事务 A调B  B无视A的事务  B总是新建一个事务按自己的来
    //     NESTED：如果当前存在事务（外部事务A的事务） 则嵌套在该事务中执行 即A调B B嵌套在A的事务中执行 但B能独立的提交和回滚
    //             如果外部事务不存在则情况和REQUIRED一样
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost post = new DiscussPost();
        post.setId(user.getId());
        post.setTitle("Hello");
        post.setContent("新人报到！");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");//人为制造错误 看上面的数据是否会提交到数据库，不做事务管理是会提交上去的
        return "OK";
    }

    //演示编程式事务
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                //新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增帖子
                DiscussPost post = new DiscussPost();
                post.setId(user.getId());
                post.setTitle("你好");
                post.setContent("新人报到！");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");//人为制造错误 看上面的数据是否会提交到数据库，不做事务管理是会提交上去的
                return "ok";
            }
        });
    }

}
