package com.jianxin.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String ,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String ,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置value的序列化方式 json格式
        template.setValueSerializer(RedisSerializer.json());
        //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();//使设置生效
        return template;
    }
}

/**
 *Redis 是一款基于键值对的nosql数据库  它的值支持多种数据结构
 * 如字符串String   哈希hashes  列表lists  集合sets  有序集合sorted sets等
 *
 * Redis将所有的数据都存放在内存中，读写性能十分惊人
 * 同时 Redis还可以将内存中的数据以快照或日志的形式保存到硬盘上，以保证数据的安全性
 * 典型应用场景 缓存 排行榜（将热门帖子缓存） 计数器 社交网络（点赞 关注） 消息队列等
 *
 *
 * redis-cli
 *
 * select 0  (有0-15个库)
 * flushdb   刷新数据库 把里面的数据清除
 * 值为String类型
 * set  test:count  1(给key取名字时redis里用:代替了下划线  test_count)
 * get  test:count
 * incr test:count （给key为test:count的值加一）
 * decr test:count （给key为test:count的值减一）
 *
 * 值为Hashes类型
 * hset test:user id 1   (key为test:user 的id字段取值为1)
 * hset test:user username zhangsan
 * hget test:user id
 * hget test:user username
 *
 * 值为lists类型
 * lpush test:ids 101 102 103  (lpush从左进 101 -> 102 101 -> 103 102 101)
 * llen test:ids   (查看有多少条数据)
 * lindex  test:ids 0 (查看key为test:ids  索引为0的值 得到103)
 * lrange  test:ids 0 2  (查看key为test:ids  索引从0到2的值 得到103  102  101)
 * rpop test:ids (从右边弹出一个值 得到101)
 * rpop test:ids (再从右边弹出一个值 得到102)
 *
 * 值为sets类型  （列表是有序的 集合是无序的 集合中的值不能重复）
 * sadd test:teachers aaa bbb ccc ddd eee
 * scard  test:teachers (统计集合中有多少个元素)
 * spop test:teachers  (从集合中随机弹出一个元素  可用于抽奖)
 * smembers test:teachers (查看集合中还有多少个元素)
 *
 * 值为sorte sets 有序集合类型
 * zadd test:students 10 aaa 20 bbb 30 ccc 40 ddd 50 eee
 * zcard test:students
 * zscore test:students ccc  (查ccc的分数)
 * zrank test:students ccc  (查ccc的分数排名 默认由小到大 得到2 默认从0开始)
 * zrange test:students 0 2  （得到aaa bbb ccc）
 *
 * keys * (查询库里所有的key)
 * keys test*  (查询库里所有test开头的key)
 *
 * type  test:user (查看key为test:user的类型)   得到hash
 * exists test:user(查看key为test:user的值存不存在  返回1代表存在  0不存在）
 * del test:user(删除key为test:user的值)
 * expire test:students 10 (设置key为test:students的值10秒后过期  可用于存放验证码)
 *
 * Spring整合Redis
 * 引入依赖spring-boot-starter-data-redis
 * 配置Redis  配置数据库参数 编写配置类  构造RedisTemplate
 * 访问Redis   redisTemplate.opsForValue()
 *             redisTemplate.opsForHash()
 *             redisTemplate.opsForList()
 *             redisTemplate.opsForSet()
 *             redisTemplate.opsForZset()
 *
 *
 */