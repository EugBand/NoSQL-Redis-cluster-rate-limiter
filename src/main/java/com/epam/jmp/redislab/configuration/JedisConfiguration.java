package com.epam.jmp.redislab.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

@Configuration
public class JedisConfiguration {

    @Bean
    // https://www.baeldung.com/jedis-java-redis-client-library
    // https://stackoverflow.com/questions/30078034/redis-cluster-in-multiple-threads
    public JedisCluster jedisCluster() {
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        jedisClusterNodes.add(new HostAndPort("127.0.0.1", 30000));
        jedisClusterNodes.add(new HostAndPort("127.0.0.1", 30001));
        jedisClusterNodes.add(new HostAndPort("127.0.0.1", 30002));
        JedisCluster jedis = new JedisCluster(jedisClusterNodes);
        return jedis;
    }

}
