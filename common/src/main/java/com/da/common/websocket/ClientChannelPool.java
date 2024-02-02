package com.da.common.websocket;

import io.netty.channel.ChannelId;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 关联ClientId和ChannelId。
 *
 */
public class ClientChannelPool {

    /** ConcurrentHashMap的最大容量是2的30次方，大概就是 10亿多。 */
    private static ConcurrentHashMap<String, ChannelId> channelIdMap;// 存储ClientId和ChannelId的映射关系

    static {
        channelIdMap = new ConcurrentHashMap<>();// 初始化ConcurrentHashMap
    }

    public ClientChannelPool() {}// 私有构造器，防止外部实例化

    /**
     * 根据clientId查询ChannelIdPool中的channelId
     *
     * @param key
     * @return
     */
    public static Optional<ChannelId> get(String key) {// 获取ChannelIdPool中的channelId
        return Optional.ofNullable(channelIdMap.get(key));// 返回channelId
    }

    /**
     * 根据channelId查询ChannelIdPool中的clientId
     *
     * @param channelId
     * @return
     */
    public static String get(ChannelId channelId) {
        // 遍历channelIdMap
        for (Map.Entry<String, ChannelId> entry : channelIdMap.entrySet()) {// 遍历channelIdMap
            if (entry.getValue().compareTo(channelId) == 0) {// 判断channelId是否相等
                return entry.getKey();// 返回clientId
            }
        }
        return null;
    }

    public static void add(String clientId, ChannelId channelId) {// 将clientId和channelId添加到ChannelIdPool中
        channelIdMap.put(clientId, channelId);// 添加clientId和channelId的映射关系
    }

    /**
     * 根据clientId删除ChannelIdPool中的数据
     *
     * @param clientId
     */
    public static void remove(String clientId) {
        channelIdMap.remove(clientId);// 删除ChannelIdPool中的clientId
    }

    /**
     * 根据channelId删除ChannelIdPool中的数据
     *
     * @param channelId
     */
    public static void remove(ChannelId channelId) {// 根据channelId删除ChannelIdPool中的数据
        // 遍历channelIdMap
        channelIdMap.entrySet().removeIf(entry -> entry.getValue().compareTo(channelId) == 0);// 判断channelId是否相等，如果相等则删除对应的clientId
    }

    public static ConcurrentHashMap<String, ChannelId> getAll() {// 获取所有clientId和channelId的映射关系
        return channelIdMap;// 返回channelIdMap
    }

}
