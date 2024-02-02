package com.da.common.config.redis;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis配置
 *
 */
@Configuration
public class RedisConfig {

    /**
     * 配置cacheManager
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 配置序列化（解决乱码的问题）

        // 配置key的序列化
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 设置默认过期时间
                .entryTtl(Duration.ofSeconds(30))// 设置缓存有效期为30秒
                // key序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))// 设置key的序列化规则
                // 值序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))// 设置value的序列化规则
                // 不允许存空值
                .disableCachingNullValues();// 设置缓存存储类型为：String（redisTemplate默认的序列化方式就是String）

        return RedisCacheManager.builder(connectionFactory)/* 设置缓存存储位置 */
                .cacheDefaults(config)/* 设置默认的过期时间 */
                .transactionAware()/* 事务 */
                .build();// 创建缓存管理器
    }

    /**
     * redisTemplate
     *
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {// 创建RedisTemplate对象
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();// 设置连接工厂
        redisTemplate.setConnectionFactory(factory);// 设置key的序列化规则
        redisTemplate.setKeySerializer(keySerializer());// 设置value的序列化规则
        redisTemplate.setHashKeySerializer(keySerializer());// 设置hashKey的序列化规则
        redisTemplate.setValueSerializer(valueSerializer());// 设置hashValue的序列化规则
        redisTemplate.setHashValueSerializer(valueSerializer());// 设置事务
        return redisTemplate;// 创建RedisCacheManager
    }


    /**
     * 键序列化 - String
     *
     * @return
     */
    private RedisSerializer<String> keySerializer() {// 创建StringRedisSerializer对象
        return new StringRedisSerializer();// 创建GenericJackson2JsonRedisSerializer对象
    }

    /**
     * 值序列化 - String
     *
     * @return
     */
    private RedisSerializer<Object> valueSerializer() {// 创建GenericJackson2JsonRedisSerializer对象
        return new GenericJackson2JsonRedisSerializer();// 创建Jackson2JsonRedisSerializer对象
    }

}
