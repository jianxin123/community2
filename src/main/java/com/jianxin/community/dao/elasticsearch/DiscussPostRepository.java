package com.jianxin.community.dao.elasticsearch;

import com.jianxin.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository//父接口ElasticsearchRepository事先定义了 对es服务器访问的crud <处理的实体类类型，实体类中的组件的类型>
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {
}
