package com.da.common.config.kafka;

import com.da.common.constant.MyConstant;
import com.da.common.entity.NoticePackage;
import com.da.common.service.kafka.consumer.PushClientService;
import com.da.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * topic监听
 *
 */
@Component
@Slf4j
public class KafkaTopicListen {

    @Autowired
    private PushClientService pushClientService;// 推送客户端

    /**
     * 监听的topic
     *
     * <p>
     * 1. 正则匹配：topicPattern = "yp.*"  表示监听以yp开头的所有topic的消息
     * 2. 手动指定：topics = {"tp.sms.topic"}  表示监听 tp.sms.topic 的topic
     *
     * @param record
     */
    // 使用 @KafkaListener 注解，监听匹配 "yp.*" 主题模式的 Kafka 消息
    @KafkaListener(topicPattern = "yp.*", containerFactory = "kafkaListenerContainerFactory")
    public void ypSmsTopicListener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        // 记录日志，显示监听到的消息信息
        log.info("监听到名称为 [{}] 的topic有消息：{}", MyConstant.YP_SMS_TOPIC,record);// 获取消息内容
        // 使用 Optional 避免空指针异常，获取 Kafka 消息的值
        Optional<String> kafkaMsg = Optional.ofNullable(record.value());// 处理消息
        kafkaMsg.ifPresent(s -> {// 处理消息
            // 将 Kafka 消息的 JSON 字符串转换为 NoticePackage 对象
            NoticePackage noticePackage = (NoticePackage) JsonUtils.toObj(s, NoticePackage.class);
            // 打印通知包信息
            System.out.println("noticePackage：" +noticePackage.toString());
            // 推送消息(消费数据)
            List<String> successReceivers = pushClientService.publishMsg(noticePackage);// 获取推送结果
            // 获取推送失败的客户端
            List<String> failReceivers = noticePackage.getReceiverChannelIds();// 判断推送结果，如果全部推送成功，则删除推送失败的客户端
            // 删除推送失败的客户端
            failReceivers.removeAll(successReceivers);
            // 判断消费是否存在失败的客户端
            if (failReceivers.size() > 0) {// 打印推送失败的客户端
                log.error("客户端消费失败的个数为 {} 个，消费失败的客户端： [{}]", failReceivers.size(), failReceivers);// 删除推送失败的客户端
            } else {
                log.info("客户端全部消费成功！");
            }
            // 手动提交offset
            ack.acknowledge();
        });
    }

}
