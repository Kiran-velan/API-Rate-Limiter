package com.example.ratelimiter.strategy;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StrategyFactory {

    private final Map<String, RateLimitingStrategy> strategies = Map.of(
            "FIXED_WINDOW", new FixedWindowStrategy(),
            "TOKEN_BUCKET", new TokenBucketStrategy() // TODO: Implement TokenBucketStrategy
    );

    public RateLimitingStrategy getStrategy(String strategyType) {
        // If strategyType is not found, return FixedWindowStrategy as default strategy
        return strategies.getOrDefault(strategyType, new FixedWindowStrategy());
    }
}
