package com.da.common.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

/**
 * Zookeeper配置
 *
 */
@Configuration
@Slf4j
public class ZkConfig {

    @Value("${zookeeper.address}")
    private String connectString;//连接地址

    @Value("${zookeeper.timeout}")
    private  int timeout;

    @Bean(name = "zkClient")
    public ZooKeeper zkClient(){
        ZooKeeper zooKeeper=null;//定义zk客户端
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);//定义闭锁
            //连接成功后，会回调watcher监听，此连接操作是异步的，执行完new语句后，直接调用后续代码
            //  可指定多台服务地址 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
            zooKeeper = new ZooKeeper(connectString, timeout, new Watcher() {//异步连接成功后回调watch监听
                @Override
                public void process(WatchedEvent event) {//收到事件通知后的回调函数（用户的业务逻辑）
                    if(Event.KeeperState.SyncConnected==event.getState()){//连接状态为已连接
                        //如果收到了服务端的响应事件,连接成功
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();//等待，直到countDown的值变为0
            log.info("【初始化ZooKeeper连接状态....】，{}",zooKeeper.getState());
        }catch (Exception e){
            log.error("【初始化ZooKeeper连接异常....】",e);
        }
        return  zooKeeper;
    }

}
