package com.jianxin.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTests {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka(){
        kafkaProducer.sendMessage("test","你好");
        kafkaProducer.sendMessage("test","再见");

        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
//生产者发送消息是主动的
@Component
class KafkaProducer{
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic,String content){
        kafkaTemplate.send(topic,content);
    }
}

//消费者消费消息是被动的 不需要手动调
@Component
class KafkaConsumer{
    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value());
    }
}

/*kafka： 分布式流媒体平台
        应用：消息系统 日志收集 用户行为追踪 流式处理
        特点：高吞吐量 消息持久化（对硬盘的顺序读写性能甚至高于对内存的随机读写
        ） 高可靠性（分布式） 高扩展性
        术语：
        Broker：kafka的服务器
        Zookeeper：管理集群
        Topic：主题  生产者把消息发布到的位置就叫主题 用于消息的分类 比如点赞的消息 关注的消息
        kafka采用发布订阅模式 与生产者消费者线程点对点模式区别
        即生产者生产一个信息后 消费者消费完就没了 之后消费的是不同的信息）
        Partition：分区 对主题进行分区
        Offset：消息在分区内存放的索引
        Leader Replica：主副本   负责响应
        Follower Replica：随从副本 只做备份不做响应

        配置
        zookeeper.properties
        dataDir=D:/kafka_2.12-2.7.0/data/zookeeper

        server.properties
        log.dirs=D:/kafka_2.12-2.7.0/data/kafka-logs

        启动zookeeper
        D:\kafka_2.12-2.7.0\kafka_2.12-2.7.0>bin\windows\zookeeper-server-start.bat config\zookeeper.properties

        启动kafka
        D:\kafka_2.12-2.7.0\kafka_2.12-2.7.0>bin\windows\kafka-server-start.bat config\server.properties

        创建主题
//--create --bootstrap-server localhost:9092 部署服务器   --replication-factor 1 副本数  --partitions 1 分区数  --topic test 主题
        D:\kafka_2.12-2.7.0\kafka_2.12-2.7.0\bin\windows>kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1   --topic test
        Created topic test.

        生产者 --broker-list localhost:9092指定往哪个服务器发布消息 --topic test 指定该服务器的哪个主题发消息
        D:\kafka_2.12-2.7.0\kafka_2.12-2.7.0\bin\windows>kafka-console-producer.bat --broker-list localhost:9092 --topic test
        >hello
        >world
        >


        消费者  --bootstrap-server localhost:9092 --topic test指定读哪个服务器的哪个主题  --from-beginning从头开始读
        D:\kafka_2.12-2.7.0\kafka_2.12-2.7.0\bin\windows>kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic test --from-beginning
        hello
        world

        Spring整合Kafka
        引入依赖 -spring-kafka
        配置kafka 在application.properties 配置server consumer
        访问kafka
        生产者 kafkaTemplate.send(topic,data);
        消费者@KafkaListener(topics = {"test"})
public void handleMessage(ConsumerRecord record){}*/









