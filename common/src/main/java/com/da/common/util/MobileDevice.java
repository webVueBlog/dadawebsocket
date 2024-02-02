package com.da.common.util;


import javax.servlet.http.HttpServletRequest;

/**
 * 获取客户端机型
 *
 */
public class MobileDevice {
    /**
     * 获取客户端机型
     * @param request
     * @return 示例:Mobile/Apple/iPhone 11
     */
    public static String getMobileDevice(HttpServletRequest request) {
        // 获取User-Agent信息
        String userAgent = request.getHeader("user-agent");
        String deviceStr = "";// 机型
        try {
            if (!userAgent.isEmpty()) {// 判断是否为空
                int startIndex = userAgent.indexOf("(");// 获取到"
                int endIndex = userAgent.indexOf(")");//"获取到"
                deviceStr = userAgent.substring(startIndex + 1, endIndex);
                return deviceStr;// 返回机型
            }
        } catch (Exception e) {
            System.out.println("获取客户端机型异常：" +e);
        }
        return null;
    }
}
