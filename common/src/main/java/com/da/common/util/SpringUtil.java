package com.da.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;//Spring应用上下文环境

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {//注入上下文到静态变量中
        if(SpringUtil.applicationContext == null) {//判断静态变量sApplicationContext是否为空
            SpringUtil.applicationContext = applicationContext;//将传入的上下文应用到静态变量中
        }
    }

    /**
     * 获取applicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {//获取上下文对象
        return applicationContext;//返回静态变量applicationContext
    }

    /**
     * 通过name获取 Bean
     *
     * @param name
     * @return
     */
    public static Object getBean(String name){//通过name获取指定的Bean
        return getApplicationContext().getBean(name);//返回Spring应用上下文环境中的Bean
    }

    /**
     * 通过class获取Bean
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz){//通过class获取Bean
        return getApplicationContext().getBean(clazz);//返回Spring应用上下文环境中的Bean
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name,Class<T> clazz){//通过name,以及Clazz返回指定的Bean
        return getApplicationContext().getBean(name, clazz);//返回Spring应用上下文环境中的Bean
    }

}