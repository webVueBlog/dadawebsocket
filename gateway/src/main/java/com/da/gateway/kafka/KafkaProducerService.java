package com.da.gateway.kafka;

import com.da.common.entity.NoticePackage;
import com.da.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Kafka生产者
 * <p>
 * 去除了ChannelIdPool容器，直接采用了netty-channel的AttributeKey记住client-id信息。
 * 消息发送不再循环调用NoticeChannelHandler.publishMsg方法，在Stream流中统一记录待接受者，
 * 生成receivers集合，将集合传给NoticeChannelHandler.publishMsg进行统一发送，然后统计发送失败的client-id，
 * 回传至任务线程，进行相应的处理。
 *
 */
@Slf4j
@Component
@Service(value = "KafkaProducer")
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final int MAX_RETRY_TIMES = 3;// 最大重试次数

    /**
     * 推送到kafka
     *
     * @param topic
     * @param noticePackage
     */
    public void publish(String topic, NoticePackage noticePackage) {
        AtomicInteger retry = new AtomicInteger(0);// 重试次数
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, JsonUtils.toJson(noticePackage));// 发送消息
        // ListenableFutureTask的异步回调
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {// 异步回调
            // 失败回调
            @Override
            public void onFailure(Throwable ex) {// 发送失败
                // 达到最大重试次数
                while (retry.get() != MAX_RETRY_TIMES) {
                    retry.incrementAndGet();// 重试次数加1
                }
                log.error("Send data to kafka failure!", ex);
            }
            // 成功回调
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Send data to kafka success!{}", result);
            }
        });
    }

}
