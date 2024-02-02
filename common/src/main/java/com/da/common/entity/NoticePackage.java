package com.da.common.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 推送消息主体
 *
 */
@Data
public class NoticePackage implements Serializable {

    /** 消息通知的Id */
    private String noticeId = UUID.randomUUID().toString();

    /** 消息主题 */
    @NotNull(message = "消息主题不能为空！")
    private String noticeLabel;

    /** 推送者 */
    @NotNull(message = "推送者不能为空！")
    private String publisher;

    /** 接收者 */
    @NotNull(message = "推送者不能为空！且要为集合！")
    private List<String> receiverChannelIds;

    /** 消息通道分组(Kafka) */
    @NotNull(message = "消息通道不能为空！")
    private String channelGroup;

    /** 消息内容 */
    @NotNull(message = "消息内不能为空！")
    private String message;

    /** 跳转url */
    private String goToUrl;

    /** 发送消息的时间 */
    private Date dateTime = new Date();
}
