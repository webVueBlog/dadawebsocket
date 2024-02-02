package com.da.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 记录失败的信息
 *
 */
public class FailClientMessage implements Serializable {

    /** 客户端的Id */
    private String clientId;

    /** channelId(Netty给的) */
    private String channelId;

    /** 服务端将其断开连接的时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date disConnectTime;

    public FailClientMessage() {
    }

    public FailClientMessage(String clientId, String channelId, Date disConnectTime) {
        this.clientId = clientId;
        this.channelId = channelId;
        this.disConnectTime = disConnectTime;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Date getDisConnectTime() {
        return disConnectTime;
    }

    public void setDisConnectTime(Date disConnectTime) {
        this.disConnectTime = disConnectTime;
    }

    @Override
    public String toString() {
        return "FailClientMessage{" +
                "clientId='" + clientId + '\'' +
                ", channelId='" + channelId + '\'' +
                ", disConnectTime=" + disConnectTime +
                '}';
    }
}
