package com.da.common.loadbalance;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 负载均衡算法
 *
 * TODO：涉及权重的轮询算法，如果服务器配置不一，需要自定义分配权重的话，还要记录Netty所在的服务器的权重，然后再写加权轮询算法、加权随机算法...
 *
 */
public class MyZkLoadBalanceUtil {

    /** 负载均衡算法计数器 */
    private static Integer roundRobinCountNum = 0;// 计数器

    /**
     * 轮询算法
     *
     * @param serverList Zookeeper中的Netty服务器列表
     * @return 返回服务器IP和端口
     */
    public static String roundRobin(List<String> serverList) {// 轮询算法
        // 加锁，保证线程安全
        String server = null;// 服务器IP和端口
        // 判断计数器是否大于服务器列表个数
        // 如果是，计数器就重新归零
        // 否则，计数器加1
        // 最后，返回服务器IP和端口
        if (roundRobinCountNum >= serverList.size()) {
            // 计数器大于服务器列表个数，计数器就重新归零
            roundRobinCountNum = 0;
            server = serverList.get(0);
        } else {
            server = serverList.get(roundRobinCountNum);
        }
        roundRobinCountNum++;
        return server;
    }

    /**
     * 随机算法
     *
     * @param serverList Zookeeper中的Netty服务器列表
     * @return 返回服务器IP和端口
     */
    public static String random(List<String> serverList) {// 随机算法
        // 随机返回服务器IP和端口
        int randomNum = (int) (Math.random() * serverList.size() - 1);// 随机数
        return serverList.get(randomNum);// 返回服务器IP和端口
    }

    /**
     * 源地址哈希算法
     *
     * @param serverList serverList Zookeeper中的Netty服务器列表
     * @param request request请求
     * @return
     */
    public static String hash(List<String> serverList, HttpServletRequest request) {// 源地址哈希算法
        // 获取真实IP和端口号
//        String remoteIp = IpUtil.getRealIp(request) + ":" +nettyPort;
        String remoteIp = "127.0.0.1:8003";// 测试用IP和端口号
        int hashCode = remoteIp.hashCode();// 获取哈希值
        // 取模得到一个值
        int serverListSize = serverList.size();// 服务器列表个数
        // 取模得到一个值
        int serverPos = hashCode % serverListSize;// 取模得到一个值
        return serverList.get(serverPos);// 返回服务器IP和端口
    }
}
