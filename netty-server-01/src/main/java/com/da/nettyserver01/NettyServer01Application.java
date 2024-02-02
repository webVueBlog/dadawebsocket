package com.da.nettyserver01;

import com.da.common.config.netty.TcpServerConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * NettyServer01Application
 *
 */
@SpringBootApplication(scanBasePackages = "com.da") // 配置包扫描路径，把common包下的也加载进去
@EnableAsync
public class NettyServer01Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(NettyServer01Application.class, args);// 启动springboot应用
        TcpServerConfigure tcpServer = context.getBean(TcpServerConfigure.class);// 获取netty服务端配置类
        try {
            tcpServer.start();// 启动netty服务端
        } catch (Exception e) {
            System.out.println("启动失败！" +e.getMessage());
        }
    }

}
