package com.example.ratelimiter.config;

import com.example.ratelimiter.middleware.RateLimiterMiddleware;
import com.example.ratelimiter.service.UserPlanService;
import com.example.ratelimiter.strategy.FixedWindowStrategy;
import com.example.ratelimiter.strategy.RateLimitingStrategy;
import com.example.ratelimiter.strategy.StrategyFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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

    @Bean
    public FilterRegistrationBean<RateLimiterMiddleware> rateLimiterFilter(UserPlanService userPlanService,
                                                                           StrategyFactory strategyFactory) {
        FilterRegistrationBean<RateLimiterMiddleware> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RateLimiterMiddleware(userPlanService, strategyFactory));
        registration.addUrlPatterns("/hello", "/api/*"); // Only apply to these paths
        registration.setOrder(1); // optional: set execution order
        return registration;
    }
}
