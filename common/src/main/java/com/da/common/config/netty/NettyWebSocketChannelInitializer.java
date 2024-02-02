package com.da.common.config.netty;

import com.da.common.constant.MyConstant;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Qualifier("ChannelInitializer")
public class NettyWebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        // 心跳检测
        ChannelPipeline pipeline = ch.pipeline();
        // 10秒内没有收到客户端请求的话就发送一个心跳检测包
        pipeline.addLast(new HttpServerCodec());// 解码request和response
        // 用于支持异步发送大的码流，但不占用过多的内存，防止发生OOM
        pipeline.addLast(new HttpObjectAggregator(65536));// 聚合器，使用websocket会用到
        // 用于处理websocket消息
        pipeline.addLast(new ChunkedWriteHandler());// 用于大数据流传输
        // websocket连接地址
        pipeline.addLast(new WebSocketServerProtocolHandler("/notice"));// 用于处理websocket消息
        // 心跳监测每隔6秒监测是否有心跳，没有心跳就将其从redis中删除并且断开连接
        //入参说明: 读超时时间、写超时时间、所有类型的超时时间、时间格式【这里的心跳监测，需要在NoticeChannelHandler之前，不然无效】
        pipeline.addLast(new IdleStateHandler(MyConstant.HEART_BEAT_INTERVAL, 0, 0, TimeUnit.SECONDS));// 空闲检测
        // 处理websocket协议
        pipeline.addLast(new NettyChannelHandler());// 自定义处理类
    }

}
