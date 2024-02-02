package com.da.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class JsonUtils<T> {// 单例模式

    private volatile static Gson gson;// 双重校验锁

    static {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();// 设置日期格式
    }

    private JsonUtils() {}// 防止被外部实例化

    public static String toJson(Object o) {// 将对象转换为json格式的字符串
        return gson.toJson(o);// 调用gson的toJson方法
    }

    @SuppressWarnings("nochecked")
    public static <T> Object toObj(String s, T t) {// 将json格式的字符串转换为对象
        return gson.fromJson(s, (Type) t);// 调用gson的fromJson方法
    }

}
