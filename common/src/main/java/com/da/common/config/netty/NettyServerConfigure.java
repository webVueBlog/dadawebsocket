package com.da.common.config.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Netty配置
 *
 */
@PropertySource(value = "classpath:netty-server.properties")
@Configuration
public class NettyServerConfigure {

    @Value("${netty.server.tcp.port}")
    private int tcpPort;// 端口号

    @Value("${netty.server.boss.thread.count}")
    private int bossCount;// boss线程组线程个数

    @Value("${netty.server.worker.thread.count}")
    private int workerCount;// worker线程组线程个数

    @Value("${netty.server.so.keepalive}")
    private boolean keepAlive;// 是否开启tcp心跳检测

    @Value("${netty.server.so.backlog}")
    private int backlog;// 拒绝连接的队列长度

    // 配置 Netty 服务器的 ServerBootstrap 的 Spring Bean
    @Bean(value = "ServerBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();// 创建 ServerBootstrap 对象
        b.group(bossGroup(), workerGroup())// 设置 bossGroup 和 workerGroup
                .channel(NioServerSocketChannel.class)// 指定使用 NIO 传输的 Channel 类型
                .handler(new LoggingHandler(LogLevel.INFO))// 添加一个用于打印日志的 LoggingHandler
                .childHandler(nettyWebSocketChannelInitializer);// 设置 childHandler，也就是子 Channel 的处理器，这里设置的是我们的自定义的 WebSocketChannelInitializer
        Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();// 获取 TCP Channel 的配置选项
        // 设置 TCP Channel 的相关配置
        Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();// 遍历配置选项并应用到 ServerBootstrap
        // 这里应用了 TCP Channel 的配置选项
        for (@SuppressWarnings("rawtypes") ChannelOption option : keySet) {
            // 设置 TCP Channel 的配置选项
            b.option(option, tcpChannelOptions.get(option));// 应用配置选项
        }
        return b;// 返回 ServerBootstrap 对象
    }

    @Autowired
    @Qualifier("ChannelInitializer")
    private NettyWebSocketChannelInitializer nettyWebSocketChannelInitializer;// 注入 NettyWebSocketChannelInitializer

    @Bean(name = "tcpChannelOptions")
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {// 创建一个配置选项的 Map 对象
        Map<ChannelOption<?>, Object> options = new HashMap<>();// 创建一个 HashMap 对象来存储配置选项
        options.put(ChannelOption.SO_KEEPALIVE, keepAlive);// 设置 SO_KEEPALIVE 选项的值为 keepAlive
        options.put(ChannelOption.SO_BACKLOG, backlog);// 设置 SO_BACKLOG 选项的值为 backlog
        return options;// 返回配置选项的 Map 对象
    }

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")// 创建一个 bossGroup 对象
    public NioEventLoopGroup bossGroup() {// 创建一个 NioEventLoopGroup 对象并设置线程数
        return new NioEventLoopGroup(bossCount);// 返回 NioEventLoopGroup 对象
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")// 创建一个 workerGroup 对象
    public NioEventLoopGroup workerGroup() {// 创建一个 NioEventLoopGroup 对象并设置线程数
        return new NioEventLoopGroup(workerCount);// 返回 NioEventLoopGroup 对象
    }

    @Bean(name = "tcpSocketAddress")// 创建一个 tcpSocketAddress 对象
    public InetSocketAddress tcpPort() {// 创建一个 InetSocketAddress 对象并设置端口号
        return new InetSocketAddress(tcpPort);// 返回 InetSocketAddress 对象
    }

}
