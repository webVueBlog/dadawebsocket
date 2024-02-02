package com.da.common.constant;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 公共常量
 *
 */
public class MyConstant {
    //---------------管道分组-------------------------------------------------
    /** 仅仅通知SMS */
    public static final ChannelGroup SMS_CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    /** 仅仅通知AI垃圾桶 */
    public static final ChannelGroup TRASH_CHANNELS_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    /** 全局通知所有 */
    public static final ChannelGroup GLOBAL_CHANNELS_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //---------------channel分组名称---------------------------------------
    public static final String YP_CHANNEL_SMS = "yp_sms_channel";
    public static final String YP_CHANNEL_TRASH = "yp_trash_channel";
    public static final String YP_CHANNEL_GLOBAL = "yp_global_channel";

    //---------------建立连接------------------------------------------------
    /** 客户端发送心跳包定死的内容 */
    public static final String HEART_BEAT = "heart_beat";
    /** 服务端间隔多少秒检查是否有心跳 */
    public static final int HEART_BEAT_INTERVAL = 6;
    /** 服务端轮询多少次没有心跳包就将其删除 */
    public static final int HEART_BEAT_DISCONNECT_POLL_NUM = 10;
    /** 刚开始与websocket建立连接，开始鉴权的时候的分割符号，也就是发送心跳包的分割符号 */
    public static final String SPLIT_CLIENT_AND_HEART_BEAT = "@#@";

    //----------------前缀------------------------------------------------
    /** 记录连接失败的客户端信息到Redis的前缀 */
    public static final String FAIL_CLIENT = "failClient_";
    /** 客户端clientId存入Redis的客户端的前缀 */
    public final static String CLIENT_PREFIX = "client_";

    //----------------Kafka的topic相关-------------------------
    /** SMS的topic */
    public static final String YP_SMS_TOPIC = "yp_sms_topic";
    /** AI智能垃圾桶的topic */
    public static final String YP_TRASH_TOPIC = "yp_trash_topic";
    /** 全局的topic */
    public static final String YP_GLOBAL_TOPIC = "yp_global_topic";

    //----------------Zookeeper相关-----------------------------
    /** 在ZK中创建的节点目录(不能递归创建)，也就是注册中心 */
    public static final String YP_NETTY_MQ = "/yp_netty_mq";
    /** Netty在ZK中的 /yp_netty_mq 注册的节点 */
    public static final String NETTY_SERVER = YP_NETTY_MQ +"/netty_server";
    /** 客户端建立握手连接之后存储的 clientId 和 channelId */
    public static final String NETTY_CLIENTID_CHANNELID = YP_NETTY_MQ +"/netty_clientId_channelId";
}
