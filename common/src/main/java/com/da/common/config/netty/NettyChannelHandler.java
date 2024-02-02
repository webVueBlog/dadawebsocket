package com.da.common.config.netty;

import com.da.common.constant.MyConstant;
import com.da.common.util.MyRedisTemplateUtil;
import com.da.common.websocket.ClientChannelPool;
import com.da.common.zk.ZookeeperUtil;
import com.da.common.entity.FailClientMessage;
import com.da.common.entity.ZkClientChannel;
import com.da.common.util.DateUtil;
import com.da.common.util.JsonUtils;
import com.da.common.util.SpringUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Netty通知事件
 *
 */
@Slf4j
public class NettyChannelHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static MyRedisTemplateUtil myRedisTemplateUtil;//redis工具类
    private static ZookeeperUtil zookeeperUtil;//zookeeper工具类

    static {
        myRedisTemplateUtil = SpringUtil.getBean(MyRedisTemplateUtil.class);//redis工具类
        zookeeperUtil = SpringUtil.getBean(ZookeeperUtil.class);//zookeeper工具类
    }

    /**
     * 心跳丢失计数器
     */
    private int counter;

    /**
     * 【收到客户端的消息的时候调用此事件】
     *
     * <p>第一次发消息和后端鉴权数据格式：需要加入的管道名称@#@clientId
     * 第一次连接需要传入客户端的clientId，客户端自定义的。比如：MAC地址、IP....一定要是唯一的！
     * <p>
     * String[] contexts = msg.text().split("@#@");
     * contexts[0]：管道名称
     * contexts[1]：客户端传来的clientId
     * <p>
     * Eg：channel_global@#@1   这个channel_global表示全局管道，1就表示用户ID，也就是接收者。
     *
     * <p>客户端发心跳检测数据：heart_beat
     * contexts[1]-heart_beat {@link MyConstant#HEART_BEAT}
     *
     * <p>分隔符为@#@
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    // 处理WebSocket消息的方法，继承自 SimpleChannelInboundHandler<TextWebSocketFrame>
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws InterruptedException, KeeperException {
        // 获取当前的Channel
        Channel channel = ctx.channel();//获取channel
        // 打印日志，显示收到的消息和对应的ChannelId
        log.info("收到客户端channelId为 [{}] 的消息 [{}] ！", channel.id().toString(), msg.text());
        // 定义消息文本的分隔符
        int splitTwo = 2;
        // 获取消息文本 // 使用分隔符将消息文本拆分成两部分
        String[] contexts = msg.text().split(MyConstant.SPLIT_CLIENT_AND_HEART_BEAT);
        // 判断拆分后的数组长度是否为2
        if (contexts.length == splitTwo) {
            // 获取ChannelGroup
            String channelGroup = contexts[0];
            // 获取客户端ID
            String clientId = contexts[1];
            // 获取ChannelId
            ChannelId channelId = channel.id();
            // 客户端首次注册，进行绑定操作
            // 封装成实体类
            ZkClientChannel zkClientChannel = new ZkClientChannel();// 创建一个ZkClientChannel对象
            // 设置客户端ID
            zkClientChannel.setClientId(clientId);
            // 设置ChannelId
            zkClientChannel.setChannelId(channelId.toString());
            // 添加接收者
            ClientChannelPool.add(clientId, channelId);
            // 关联对应管道
            addToChannelGroup(channelGroup, channel);
            // 判断是否已经存在该客户端ID的绑定关系
            // 检查是否成功关联到Channel Group
            if (hasAddChannelGroup(channelGroup, channelId)) {// 判断是否已经存在该客户端ID的绑定关系
                // 客户端首次注册，进行绑定操作
                // clientId 和 channelId 存储在Zookeeper临时节点上
                // 在Zookeeper中创建临时节点，存储clientId和channelId
                zookeeperUtil.createTemporaryNode(MyConstant.NETTY_CLIENTID_CHANNELID +"/" +clientId, zkClientChannel.toString());
                // 客户端信息redis也存一份
                myRedisTemplateUtil.set("client_list:" +clientId, JsonUtils.toJson(zkClientChannel));
                // 鉴权成功
                TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame("鉴权成功！");
                // 发送消息
                channel.writeAndFlush(textWebSocketFrame);
            } else {
                log.error("鉴权异常！");
                TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame("鉴权异常！");
                channel.writeAndFlush(textWebSocketFrame);
                ctx.channel().close().sync();// 关闭通道
            }
        } else if (msg.text().equals(MyConstant.HEART_BEAT)) {// 心跳检测
            // 客户端发送心跳，重置计数器
            log.info("收到客户端channelId为 [{}] 的心跳包!", channel.id());
            counter = 0;
        } else if (zookeeperUtil.exists(MyConstant.NETTY_CLIENTID_CHANNELID + "/" + channel.id().toString(), false) == null) {
            // 如果ChannelId对应的Zookeeper节点不存在，关闭连接
            // 关如果没有绑定的，直接关闭此TCP连接
            log.error("channelId为 [{}] 存在非法操作！已断开连接！", channel.id().toString());// 关闭通道
            TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame("存在非法操作！已断开连接！");// 发送消息
            channel.writeAndFlush(textWebSocketFrame);// 发送消息
            ctx.channel().close().sync();// 关闭通道
        }
    }

    /**
     * 心跳监测
     * 主要用于监测心跳，当丢失心跳次数达到一定阈值时，关闭连接并进行相应的清理操作
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {// 心跳监测
        // ChannelHandlerContext要是心跳机制事件类型IdleStateEvent
        LocalDateTime localDateTime = LocalDateTime.now();// 获取当前时间
        log.info("当前轮询时间 [{}]", DateUtil.localDateToString(localDateTime));// 打印当前时间
        Channel channel = ctx.channel();// 获取通道
        if (evt instanceof IdleStateEvent) {// 判断事件类型是否为空闲事件
            if (counter >= MyConstant.HEART_BEAT_DISCONNECT_POLL_NUM) {// 如果轮询次数大于等于最大轮询次数
                log.error("已经轮询 [{}] 次没收到客户端channelId为 [{}] 的心跳了！已将其断开连接并删除！",
                        MyConstant.HEART_BEAT_DISCONNECT_POLL_NUM, channel.id());
                // 获取客户端ID
                String clientId = ClientChannelPool.get(channel.id());
                // 记录错误信息，只记录ChannelIdPool中与业务关联的[已鉴权的]
                if (!StringUtils.isEmpty(clientId)) {
                    // 从Redis中获取客户端信息
                    String channelIdFromRedis = myRedisTemplateUtil.get("client_list:" +clientId);
                    log.error("已移除clientId为 [{}] 的客户端！", channelIdFromRedis);
                    // 删除Redis中的数据与此关联的数据
                    myRedisTemplateUtil.delete(channelIdFromRedis);
                    // 移除管道中的
                    removeToChannelGroupByChannel(channelIdFromRedis, channel);// 移除通道
                    // 记录错误信息
                    FailClientMessage failClientMessage = new FailClientMessage();// 创建错误信息对象
                    System.out.println("clientId：" + clientId);// 打印客户端ID
                    failClientMessage.setClientId(clientId);// 设置客户端ID
                    failClientMessage.setChannelId(channel.id().toString());// 设置通道ID
                    failClientMessage.setDisConnectTime(new Date());// 设置断开时间
                    myRedisTemplateUtil.set(MyConstant.FAIL_CLIENT +":" +clientId, JsonUtils.toJson(failClientMessage));// 保存错误信息
                }
                // 移除与此关联的ChannelIdPool
                ClientChannelPool.remove(channel.id());// 移除通道
                // 关闭此TCP连接
                ctx.channel().close().sync();// 关闭连接
                counter = 0;// 重置计数器
            } else {
                counter++;// 计数器加1
                log.error("客户端channelId为 [{}] 开始已经丢失 [{}] 次心跳包！", ctx.channel().id(), counter);// 打印错误信息
            }
        }
    }

    /**
     * 关闭tcp连接
     * <p>
     * 当一个TCP连接关闭后，对应的Channel会自动从ChannelGroup移除，所以不需要手动去移除关闭的Channel。
     * <p>
     * 如果非要移除的话：ctx.channel().close().sync();
     * 就可以将此客户端的ChannelHandlerContext移除！
     * 执行一些资源的清理工作或者在处理器从 ChannelPipeline 中移除时执行一些特定的操作
     * @param ctx
     * @throws Exception
     */
    // 当 ChannelHandler 从 ChannelPipeline 中移除时调用的方法
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 打印日志，表示该 ChannelHandler 被移除
        log.info("handlerRemoved...");

        // 调用父类的 handlerRemoved 方法
        super.handlerRemoved(ctx);
    }


    /**
     * 接入了新连接
     * <p>
     * 加入管道的逻辑放在客户端连接之后，发送指定格式的消息再加。
     *
     * @param ctx
     * @throws Exception
     */
    // 当与远程对等体建立连接时调用的方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 获取当前 Channel
        Channel channel = ctx.channel();

        // 获取当前 Channel 的唯一标识
        ChannelId channelId = channel.id();

        // 打印日志，表示有新的客户端连接
        log.info("新接入channelId为 [{}] 的客户端", channelId);
    }


    /**
     * 客户端断开连接
     *
     * TODO：定时任务扫描Redis中的客户端在Zookeeper中不存在的，然后将其删除！
     *
     * @param ctx
     * @throws Exception
     */
    // 当与远程对等体的连接断开时调用的方法
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 获取当前 Channel 的唯一标识
        ChannelId channelId = ctx.channel().id();

        // 打印日志，表示与客户端的连接已断开
        log.info("客户端channelId为 [{}] 已断开链接", channelId);

        // 从ChannelIdPool中移除对应的channelId
        ClientChannelPool.remove(channelId);

        // 关闭当前 Channel
        ctx.channel().close().sync();

        // 调用父类的 channelInactive 方法
        super.channelInactive(ctx);
    }


    /**
     * 客户端报错
     * 当 ChannelPipeline 中的某个 ChannelHandler 抛出异常时调用的方法
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端channelId为 [{}] 报错：[{}]", ctx.channel().id(), cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 校验是否鉴权成功，是否加入至Netty管道
     *
     * @param channelGroup channelGroup
     * @return
     */
    public static boolean hasAddChannelGroup(String channelGroup, ChannelId clientId) {
        // 判断是否加入至Netty管道
        switch (channelGroup) {
            case MyConstant.YP_CHANNEL_SMS:// 短信通道
                // 判断是否加入至Netty管道
                if (MyConstant.SMS_CHANNEL_GROUP.find(clientId) != null) {
                    return true;
                }
            case MyConstant.YP_CHANNEL_TRASH:// 垃圾通道
                // 判断是否加入至Netty管道
                if (MyConstant.TRASH_CHANNELS_GROUP.find(clientId) != null) {
                    return true;
                }
            case MyConstant.YP_CHANNEL_GLOBAL:// 全局通道
                // 判断是否加入至Netty管道
                if (MyConstant.GLOBAL_CHANNELS_GROUP.find(clientId) != null) {
                    return true;
                }
            default:
                return false;
        }
    }

    /**
     * 加入管道
     *
     * @param type
     * @param channel
     */
    public static void addToChannelGroup(String type, Channel channel) {
        if (type.equals(MyConstant.YP_CHANNEL_SMS)) {// 短信通道
            MyConstant.SMS_CHANNEL_GROUP.add(channel);// 加入至Netty管道
            System.out.println("YP_SMS_TOPIC");
        } else if (type.equals(MyConstant.YP_CHANNEL_TRASH)) {// 垃圾通道
            MyConstant.TRASH_CHANNELS_GROUP.add(channel);// 加入至Netty管道
            System.out.println("YP_TRASH_TOPIC");
        } else if (type.equals(MyConstant.YP_CHANNEL_GLOBAL)) {// 全局通道
            MyConstant.GLOBAL_CHANNELS_GROUP.add(channel);
            System.out.println("YP_GLOBAL_TOPIC");
        }
    }

    /**
     * 从管道移除
     *
     * @param type
     * @param channel
     */
    private void removeToChannelGroupByChannel(String type, Channel channel) {
        if (type.equals(MyConstant.YP_CHANNEL_SMS)) {// 短信通道
            MyConstant.SMS_CHANNEL_GROUP.remove(channel);
        } else if (type.equals(MyConstant.YP_CHANNEL_TRASH)) {// 垃圾通道
            MyConstant.TRASH_CHANNELS_GROUP.remove(channel);
        } else if (type.equals(MyConstant.YP_CHANNEL_GLOBAL)) {// 全局通道
            MyConstant.GLOBAL_CHANNELS_GROUP.remove(channel);
        }
    }

}
