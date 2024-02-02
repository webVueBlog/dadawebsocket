package com.da.common.config.kafka;

import com.da.common.constant.MyConstant;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * 程序启动的时候创建topic
 *
 * TODO：从数据库中加载出需要创建的topic
 *
 */
@Configuration
public class KafkaTopicConfig {//kafka配置文件

//    @Value("${spring.kafka.producer.topic}")
//    private String topic;//kafka主题
//
//    @Value("${spring.kafka.producer.partitions}")
//    private int partitions;//分区数
//
//    @Value("${spring.kafka.producer.replicas}")
//    private int replicas;//副本数

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServers;//kafka地址

    /**
     * KafkaAdmin
     *
     * @param properties
     * @return
     */
    // 使用 @Bean 注解，将 KafkaAdmin 配置为 Spring Bean
    @Bean
    public KafkaAdmin admin(KafkaProperties properties) {
        // 通过 KafkaProperties 对象构建 KafkaAdmin，其中包含 Kafka 的配置信息
        KafkaAdmin admin = new KafkaAdmin(properties.buildAdminProperties());

        // 设置当无法连接到 Kafka Broker 时，抛出致命错误
        admin.setFatalIfBrokerNotAvailable(true);

        // 返回创建的 KafkaAdmin Bean
        return admin;
    }


    /**
     * SMS的topic
     *
     * @return
     */
    // 使用 @Bean 注解，将 ypSmsTopic 配置为 Spring Bean
    @Bean
    public NewTopic ypSmsTopic() {
        // 将 Kafka 服务器字符串拆分为数组
        String[] kafkaServersStr = this.kafkaServers.split(",");

        // 创建一个新的 Kafka Topic，传入队列名称、分区数（服务器数量的两倍）、副本个数（服务器数量）
        return new NewTopic(MyConstant.YP_SMS_TOPIC, kafkaServersStr.length * 2, (short) kafkaServersStr.length);
    }


    /**
     * AI垃圾桶topic
     *
     * @return
     */
    // 使用 @Bean 注解，将 ypTrashTopic 配置为 Spring Bean
    @Bean
    public NewTopic ypTrashTopic() {
        // 将 Kafka 服务器字符串拆分为数组
        String[] kafkaServersStr = this.kafkaServers.split(",");

        // 创建一个新的 Kafka Topic，传入队列名称、分区数（服务器数量的两倍）、副本个数（服务器数量）
        return new NewTopic(MyConstant.YP_TRASH_TOPIC, kafkaServersStr.length * 2, (short) kafkaServersStr.length);
    }


    /**
     * 全局topic
     *
     * @return
     */
    // 使用 @Bean 注解，将 ypGlobalTopic 配置为 Spring Bean
    @Bean
    public NewTopic ypGlobalTopic() {
        // 将 Kafka 服务器字符串拆分为数组
        String[] kafkaServersStr = this.kafkaServers.split(",");

        // 创建一个新的 Kafka Topic，传入队列名称、分区数（服务器数量的两倍）、副本个数（服务器数量）
        return new NewTopic(MyConstant.YP_GLOBAL_TOPIC, kafkaServersStr.length * 2, (short) kafkaServersStr.length);
    }

}
