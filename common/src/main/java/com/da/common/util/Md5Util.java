package com.da.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

    /**
     * 转换为 md5 32位小写
     *
     * @param str
     * @return
     */
    public static String to32LowerCase(String str) {
        // 创建一个MessageDigest实例来使用MD5算法
        MessageDigest messageDigest = null;
        try {
            // 创建一个MessageDigest实例来使用MD5算法
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();// 重置摘要
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));// 使用指定的字节更新摘要
        } catch (NoSuchAlgorithmException e) {// 如果没有找到MD5算法
            e.printStackTrace();// 打印异常信息
        }
        byte[] byteArray = messageDigest.digest();// 计算摘要
        StringBuilder md5StrBuff = new StringBuilder();// 创建一个StringBuilder实例来存储结果
        for (byte b : byteArray) {// 遍历每个字节
            if (Integer.toHexString(0xFF & b).length() == 1) {// 如果转换后的字节数是1位，则前面补0
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & b));// 将转换后的字节数追加到StringBuilder实例中
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & b));// 将转换后的字节数追加到StringBuilder实例中
            }
        }
        return md5StrBuff.toString();// 返回转换后的字符串
    }

    public static void main(String[] args) {// 测试方法
        System.out.println(to32LowerCase("123456"));;
    }
}
