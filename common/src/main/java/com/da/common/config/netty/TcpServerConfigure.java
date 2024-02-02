package com.da.common.config.netty;

import com.da.common.constant.MyConstant;
import com.da.common.util.IpUtil;
import com.da.common.zk.ZookeeperUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Component
@Slf4j
public class TcpServerConfigure {// 配置类，用于配置Netty服务端

    @Autowired
    @Qualifier("ServerBootstrap")
    private ServerBootstrap serverBootstrap;// Netty服务端启动引导类

    /** 包含了Netty启动的时候一些主机和端口等信息 */
    @Autowired
    @Qualifier("tcpSocketAddress")
    private InetSocketAddress tcpPort;// Netty服务端监听的端口

    private Channel serverChannel;// Netty服务端监听的端口

    /** Zookeeper Api操作 */
    @Autowired
    private ZookeeperUtil zookeeperUtil;// 用于操作Zookeeper

    @Value("${netty.server.tcp.port}")
    private String nettyServerTcpPort;// Netty服务端监听的端口

    /**
     * 启动Netty
     *
     * @throws Exception
     */
    public void start() throws Exception {// 启动Netty服务端
        // 获取本主机IP地址
        String hostIp = IpUtil.getHostIp();// 获取本主机IP地址
        // 0.0.0.0/0.0.0.0:8003
        System.out.println("tcpPort: " +tcpPort.toString());
        String nettyServerPort = hostIp +":" +nettyServerTcpPort;// 获取本主机IP地址和端口
        boolean node = zookeeperUtil.createTemporaryNode(MyConstant.NETTY_SERVER + "/" + nettyServerPort, nettyServerPort);// 创建临时节点，表示服务注册
        if (node) {// 服务注册成功
            log.info("服务 [{}] 注册成功！", tcpPort.toString());
        } else {
          log.error("服务 [{}] 注册失败！", tcpPort.toString());
        }
        // 在此之前做操作
        serverChannel =  serverBootstrap.bind(tcpPort).sync().channel().closeFuture().sync().channel();// 绑定端口，启动服务端
    }

    @PreDestroy
    public void stop() throws Exception {// 停止Netty服务端
        serverChannel.close();// 关闭服务端通道
        serverChannel.parent().close();// 关闭服务端通道的父通道
    }

    public ServerBootstrap getServerBootstrap() {// 获取ServerBootstrap对象
        return serverBootstrap;// 设置ServerBootstrap对象
    }

    public void setServerBootstrap(ServerBootstrap serverBootstrap) {// 设置ServerBootstrap对象
        this.serverBootstrap = serverBootstrap;
    }

    public InetSocketAddress getTcpPort() {// 获取TCP端口
        return tcpPort;
    }

    public void setTcpPort(InetSocketAddress tcpPort) {// 设置TCP端口
        this.tcpPort = tcpPort;
    }

}
