package com.example.ratelimiter.strategy;

import com.example.ratelimiter.model.UserPlan;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class RedisFixedWindowStrategy implements RateLimitingStrategy {

    private final StringRedisTemplate redisTemplate;

    public RedisFixedWindowStrategy(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean allowRequest(String userId, UserPlan plan) {
        long window = Instant.now().getEpochSecond() / plan.getWindowInSeconds();
        String key = "rate:" + userId + ":" + window;

        Long currentCount = redisTemplate.opsForValue().increment(key);
        if (currentCount == 1) {
            redisTemplate.expire(key, plan.getWindowInSeconds(), TimeUnit.SECONDS);
        }

        return currentCount <= plan.getLimit();
    }
}
