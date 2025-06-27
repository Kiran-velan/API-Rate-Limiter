package com.example.ratelimiter.strategy;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StrategyFactory {

//    private final Map<String, RateLimitingStrategy> strategies = Map.of(
//            "FIXED_WINDOW", new FixedWindowStrategy(),
//            "TOKEN_BUCKET", new TokenBucketStrategy()
//    );
//
//    public RateLimitingStrategy getStrategy(String strategyType) {
//        // If strategyType is not found, return FixedWindowStrategy as default strategy
//        return strategies.getOrDefault(strategyType, new FixedWindowStrategy());
//    }

    private final Map<String, RateLimitingStrategy> strategies;

    public StrategyFactory(StringRedisTemplate redisTemplate) {
        this.strategies = Map.of(
                "FIXED_WINDOW", new RedisFixedWindowStrategy(redisTemplate),
                "TOKEN_BUCKET", new RedisTokenBucketStrategy(redisTemplate) // not implemented yet
        );
    }

    public RateLimitingStrategy getStrategy(String strategyType) {
        return strategies.getOrDefault(strategyType, new RedisFixedWindowStrategy(new StringRedisTemplate()));
    }
}
