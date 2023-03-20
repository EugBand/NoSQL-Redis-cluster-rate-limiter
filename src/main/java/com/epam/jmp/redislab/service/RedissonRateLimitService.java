package com.epam.jmp.redislab.service;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.epam.jmp.redislab.api.RequestDescriptor;
import com.epam.jmp.redislab.configuration.ratelimit.RateLimitRule;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;

@Component
public class RedissonRateLimitService implements RateLimitService {

    @Autowired
    ProxyManager<String> proxyManager;

    @Autowired
    Set<RateLimitRule> rateLimitRules;

    @Override
    public boolean shouldLimit(Set<RequestDescriptor> requestDescriptors) {
        boolean islimited = false;
        for (RequestDescriptor descriptor : requestDescriptors) {
            Bucket bucket = resolveBucket(descriptor);
            if (!bucket.tryConsume(1)) {
                islimited = true;
            }
        }
        return islimited;
    }


    private Bucket resolveBucket(RequestDescriptor descriptor) {
        String key = assembleKey(descriptor);
        Supplier<BucketConfiguration> configSupplier = getConfigSupplier(key, descriptor);
        return proxyManager.builder().build(key, configSupplier);
    }

    private Supplier<BucketConfiguration> getConfigSupplier(String key, RequestDescriptor descriptor) {
        RateLimitRule rule = getAccount(key, descriptor);
        Integer requestLimit = Objects.requireNonNull(rule).getAllowedNumberOfRequests();
        Refill refill = Refill.intervally(requestLimit, Duration.ofSeconds(getDuration(rule)));
        Bandwidth limit = Bandwidth.classic(requestLimit, refill);
        return () -> (BucketConfiguration.builder()
                .addLimit(limit)
                .build());
    }
    @Cacheable(value = "RateLimitRules", key = "#key")
    private RateLimitRule getAccount(String key, RequestDescriptor descriptor) {
        RateLimitRule defaultRule =
                rateLimitRules.stream()
                        .filter(r -> "".equals(r.getAccountId().orElse(null)))
                        .filter(r -> !r.getRequestType().isPresent())
                        .findFirst().orElse(null);
        Stream<RateLimitRule> limitRules;
        if (descriptor.getAccountId().isPresent()) {
            limitRules = rateLimitRules.stream().filter(r -> r.getAccountId().equals(descriptor.getAccountId()));
        } else {
            limitRules = rateLimitRules.stream().filter(r -> r.getClientIp().equals(descriptor.getClientIp()));
        }
        if (descriptor.getRequestType().isPresent()) {
            return limitRules.filter(r -> r.getClientIp().equals(descriptor.getRequestType())).findFirst().orElse(defaultRule);
        }
        return limitRules.findFirst().orElse(defaultRule);
    }


}
