package com.example.ratelimiter.config;

import com.example.ratelimiter.strategy.FixedWindowStrategy;
import com.example.ratelimiter.strategy.RateLimitingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

    @Bean
    public RateLimitingStrategy rateLimitingStrategy() {
        // Example: Allow 5 requests per 10 seconds
        return new FixedWindowStrategy(5, 10);
    }
}
