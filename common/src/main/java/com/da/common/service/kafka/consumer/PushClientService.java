package com.da.common.service.kafka.consumer;

import com.da.common.websocket.ClientChannelPool;
import com.da.common.constant.MyConstant;
import com.da.common.entity.NoticePackage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 往客户端发送消息
 *
 */
@Service
@Slf4j
public class PushClientService {

    /**
     * 往不同的通道发送数据
     *
     * @param noticePackage
     * @return 返回推送成功的客户端ID集合
     */
    public List<String> publishMsg(NoticePackage noticePackage) {
        // 获取通道组
        System.out.println("noticePackage.getReceiverChannelIds()1111111：" +noticePackage.getReceiverChannelIds());
        // 获取通道
        List<String> successReceivers = new ArrayList<>();// 成功推送的客户端ID集合
        // 遍历通道组
        if (noticePackage.getReceiverChannelIds() != null) {// 判断是否需要推送
            // 遍历通道组
            for (String receiver : noticePackage.getReceiverChannelIds()) {// 遍历需要推送的客户端ID集合
                // 获取客户端通道
                ClientChannelPool.get(receiver).ifPresent(receiverP -> {// 获取客户端通道
                    // 获取通道组
                    System.out.println("noticePackage.getChannelGroup()：" +noticePackage.getChannelGroup());// 判断是否需要推送
                    // 判断是否需要推送
                    if (noticePackage.getChannelGroup().equals(MyConstant.YP_SMS_TOPIC)// 判断是否需要推送
                            && MyConstant.SMS_CHANNEL_GROUP.find(receiverP) != null) {// 判断是否需要推送
                        System.out.println("YP_CHANNEL_SMS---------------");// 判断是否需要推送
                        send(receiver, MyConstant.SMS_CHANNEL_GROUP, noticePackage);// 判断是否需要推送
                    }
                    if (noticePackage.getChannelGroup().equals(MyConstant.YP_TRASH_TOPIC)// 判断是否需要推送
                            && MyConstant.SMS_CHANNEL_GROUP.find(receiverP) != null) {// 判断是否需要推送
                        System.out.println("YP_CHANNEL_TRASH-------------");// 判断是否需要推送
                        send(receiver, MyConstant.TRASH_CHANNELS_GROUP, noticePackage);// 判断是否需要推送
                    }
                    if (noticePackage.getChannelGroup().equals(MyConstant.YP_GLOBAL_TOPIC)
                            && MyConstant.SMS_CHANNEL_GROUP.find(receiverP) != null) {
                        System.out.println("YP_CHANNEL_GLOBAL--------------");
                        send(receiver, MyConstant.GLOBAL_CHANNELS_GROUP, noticePackage);
                    }
                    successReceivers.add(receiver);// 判断是否需要推送
                });
            }
        }
        System.out.println("noticePackage.getReceiverChannelIds()222222：" +successReceivers);
        return successReceivers;// 判断是否需要推送
    }

    /**
     * 推送数据到客户端
     *
     * @param receiver
     * @param channels
     * @param noticePackage
     */
    private void send(String receiver, ChannelGroup channels, NoticePackage noticePackage) {
        // 获取客户端连接
        TextWebSocketFrame frame = new TextWebSocketFrame(noticePackage.getMessage());
        // 推送数据
        final Channel[] channel = {null};
        // 获取客户端连接
        Optional<ChannelId> optionalChannelId = ClientChannelPool.get(receiver);
        // 判断连接是否存在
        optionalChannelId.ifPresent(channelId -> {// 存在连接
            // 获取连接
            channel[0] = channels.find(channelId);// 获取连接
            // 判断连接是否存在
            channel[0].writeAndFlush(frame);// 推送数据
        });
    }
}
