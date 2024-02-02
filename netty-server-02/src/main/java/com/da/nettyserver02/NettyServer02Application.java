package com.da.nettyserver02;

import com.da.common.config.netty.TcpServerConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * nettyserver02Application
 *
 */
@SpringBootApplication(scanBasePackages = "com.da")
@EnableAsync
public class NettyServer02Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(NettyServer02Application.class, args);//启动springboot
        TcpServerConfigure tcpServer = context.getBean(TcpServerConfigure.class);//获取配置文件中的bean
        try {
            tcpServer.start();//启动netty服务端
        } catch (Exception e) {
            System.out.println("启动失败！" +e.getMessage());
        }
    }

}
