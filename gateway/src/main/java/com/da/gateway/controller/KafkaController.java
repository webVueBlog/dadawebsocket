package com.da.gateway.controller;

import com.da.common.constant.MyConstant;
import com.da.common.entity.NoticePackage;
import com.da.common.enums.MyHttpCode;
import com.da.common.vo.ResultData;
import com.da.gateway.kafka.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/api/v1/kafka")
public class KafkaController {

    @Autowired
    private KafkaProducerService kafkaProducerService;// 注入Kafka生产者服务

    /**
     * 推送消息至Kafka集群
     *
     * @param noticePackage 消息内容
     * @return 返回成功或失败以及信息
     */
    @PostMapping("pushMsgToMq")
    public ResultData<String> pushMsgToMq(@RequestBody @Valid NoticePackage noticePackage) {
        try {
            System.out.println("noticePackage：" +noticePackage);
            if (noticePackage.getChannelGroup().equals(MyConstant.YP_SMS_TOPIC)) {// 判断渠道组
                System.out.println("YP_SMS_TOPIC");
                kafkaProducerService.publish(MyConstant.YP_SMS_TOPIC, noticePackage);// 推送消息至Kafka集群
            }
            if (noticePackage.getChannelGroup().equals(MyConstant.YP_TRASH_TOPIC)) {// 判断渠道组
                System.out.println("YP_TRASH_TOPIC");
                kafkaProducerService.publish(MyConstant.YP_TRASH_TOPIC, noticePackage);// 推送消息至Kafka集群
            }
            if (noticePackage.getChannelGroup().equals(MyConstant.YP_GLOBAL_TOPIC)) {// 判断渠道组
                System.out.println("YP_GLOBAL_TOPIC");
                kafkaProducerService.publish(MyConstant.YP_GLOBAL_TOPIC, noticePackage);// 推送消息至Kafka集群
            }
            return new ResultData<>(MyHttpCode.OK.getCode(), MyHttpCode.OK.getMsg());// 返回成功信息
        } catch (Exception e) {
            log.error("推送消息到Kafka集群失败！", e);
            return new ResultData<>(MyHttpCode.ERROR.getCode(), "推送消息到Kafka集群失败！" + e.getMessage());
        }
    }

}
