package com.epam.jmp.redislab.configuration.ratelimit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.Optional;

import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;

@REntity
public class RateLimitRule {

    @RId
    private final String key;

    private final Optional<String> accountId;

    private final Optional<String> clientIp;

    private final Optional<String> requestType;

    private final Integer allowedNumberOfRequests;

    private final RateLimitTimeInterval timeInterval;

    @JsonCreator
    public RateLimitRule(@JsonProperty("accountId") Optional<String> accountId,
                         @JsonProperty("clientIp") Optional<String> clientIp,
                         @JsonProperty("requestType") Optional<String> requestType,
                         @JsonProperty("allowedNumberOfRequests") Integer allowedNumberOfRequests,
                         @JsonProperty("timeInterval") RateLimitTimeInterval timeInterval) {
        this.accountId = accountId;
        this.clientIp = clientIp;
        this.requestType = requestType;
        this.allowedNumberOfRequests = allowedNumberOfRequests;
        this.timeInterval = timeInterval;
        this.key = this.getAccountId().orElse("") + this.getClientIp().orElse("") + this.getRequestType().orElse("");
    }

    public Optional<String> getAccountId() {
        return accountId;
    }

    public Optional<String> getClientIp() {
        return clientIp;
    }

    public Integer getAllowedNumberOfRequests() {
        return allowedNumberOfRequests;
    }

    public String getKey() {
        return key;
    }

    public RateLimitTimeInterval getTimeInterval() {
        return timeInterval;
    }

    public Optional<String> getRequestType() {
        return requestType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RateLimitRule that = (RateLimitRule) o;
        return accountId.equals(that.accountId)
                && clientIp.equals(that.clientIp)
                && requestType.equals(that.requestType)
                && allowedNumberOfRequests.equals(that.allowedNumberOfRequests)
                && timeInterval == that.timeInterval;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, clientIp, requestType, allowedNumberOfRequests, timeInterval);
    }
}
