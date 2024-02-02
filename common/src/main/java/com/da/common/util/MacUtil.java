package com.da.common.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * 获取客户端的MAC地址
 *
 */
public class MacUtil {

    public static String getMACAddress(String ip){//获取MAC地址
        String str = "";//用来保存获取到的MAC地址
        String macAddress = "";//最终的MAC地址
        try {
            Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);//获取MAC地址的命令
            InputStreamReader ir = new InputStreamReader(p.getInputStream());//由获取到的输入流创建一个输入流读取器
            LineNumberReader input = new LineNumberReader(ir);//输入流读取器
            for (int i = 1; i < 100; i++) {//读取前100行
                str = input.readLine();//读取一行
                if (str != null) {//如果str不为空
                    if (str.indexOf("MAC Address") > 1) {//如果这一行包含“MAC Address”
                        macAddress = str.substring(str.indexOf("MAC Address") + 14, str.length());//取出MAC地址
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);//异常处理
        }
        return macAddress;//返回MAC地址
    }

}
