package com.example.ratelimiter.config;

import com.example.ratelimiter.strategy.FixedWindowStrategy;
import com.example.ratelimiter.strategy.RateLimitingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RateLimiterConfig {
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        // For future Redis-based strategies
//        return null;
//    }

    // Add Prometheus/Actuator metrics beans here later
}
