package com.da.gateway.controller;

import com.da.common.constant.MyConstant;
import com.da.common.entity.ConnectNettyServer;
import com.da.common.loadbalance.MyZkLoadBalanceUtil;
import com.da.common.vo.ResultData;
import com.da.gateway.zk.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/connect")
public class ConnectNettyController {

    @Autowired
    private ZookeeperUtil zookeeperUtil;// 连接Zookeeper

    /**
     * 负载均衡算法 -> 获取Zookeeper中的Netty一个地址
     *
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    @GetMapping("getNettyServer")
    public ResultData<ConnectNettyServer> getServer() throws KeeperException, InterruptedException {// 获取Netty服务器的地址
        List<String> nettyServerList = zookeeperUtil.getChildrenArrayData(MyConstant.NETTY_SERVER, null);// 获取Netty服务器地址列表
        String nettyServer = MyZkLoadBalanceUtil.roundRobin(nettyServerList);// 负载均衡算法
        ConnectNettyServer connectNettyServer = new ConnectNettyServer();// 封装Netty服务器地址
        connectNettyServer.setNettyServerUrl(nettyServer);// 设置Netty服务器地址
        log.info("客户端请求的Netty服务器地址 [{}]", connectNettyServer);// 打印Netty服务器地址
        return new ResultData<>(connectNettyServer);// 返回Netty服务器地址
    }
}
