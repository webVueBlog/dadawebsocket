package com.da.common.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IpUtil {

    /**
     * 获取真实IP
     *
     * @param request
     * @return
     */
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");// 获取X-Forwarded-For请求头
        // 如果请求头中没有X-Forwarded-For，则将其设置为null
        return checkIp(ip) ? ip : (// 如果请求头中没有X-Forwarded-For，则获取Proxy-Client-IP
                checkIp(ip = request.getHeader("Proxy-Client-IP")) ? ip : (// 如果请求头中没有Proxy-Client-IP，则获取WL-Proxy-Client-IP
                        checkIp(ip = request.getHeader("WL-Proxy-Client-IP")) ? ip :// 如果请求头中没有WL-Proxy-Client-IP，则获取HTTP_CLIENT_IP
                                request.getRemoteAddr()));// 如果请求头中没有HTTP_CLIENT_IP，则获取HTTP_X_FORWARDED_FOR
    }

    /**
     * 校验IP
     *
     * @param ip
     * @return
     */
    private static boolean checkIp(String ip) {
        return !StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip);// 如果IP不为空且不是"unknown"，则返回true
    }

    /**
     * 获取本机IP
     *
     * @return
     */
    public static String getHostIp() {// 获取本机IP
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();// 获取所有网络接口
            while (allNetInterfaces.hasMoreElements()) {// 遍历所有网络接口
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();// 获取网络接口名称
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();// 获取网络接口下的所有IP
                while (addresses.hasMoreElements()) {// 遍历所有IP
                    InetAddress ip = (InetAddress) addresses.nextElement();// 过滤出IPv4地址
                    if (ip != null
                            && ip instanceof Inet4Address
                            //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1) {// 返回IPv4地址
                        System.out.println("本机的IP = " + ip.getHostAddress());
                        return ip.getHostAddress();// 返回IPv4地址
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
