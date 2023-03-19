package com.epam.jmp.redislab.configuration;

import java.time.Duration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import redis.clients.jedis.JedisPool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LettuceConfiguration {

    @Bean
    //https://lettuce.io/core/release/reference/#_connection_pooling
    public StatefulRedisClusterConnection<String,String> lettuceRedisClusterConnection(){
        RedisClusterClient redisClient = RedisClusterClient.create("redis://redis-master-0:30000");
        StatefulRedisClusterConnection<String, String> connection = redisClient.connect();
        return connection;
    }
}
