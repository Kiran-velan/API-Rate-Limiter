package com.example.ratelimiter.config;

import com.example.ratelimiter.middleware.RateLimiterMiddleware;
import com.example.ratelimiter.service.RequestLogService;
import com.example.ratelimiter.service.RequestMetricsService;
import com.example.ratelimiter.service.UserPlanService;
import com.example.ratelimiter.strategy.StrategyFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

    @Bean
    public FilterRegistrationBean<RateLimiterMiddleware> rateLimiterFilter(UserPlanService userPlanService,
                                                                           StrategyFactory strategyFactory,
                                                                           RequestLogService requestLogService,
                                                                           RequestMetricsService requestMetricsService) {
        FilterRegistrationBean<RateLimiterMiddleware> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RateLimiterMiddleware(userPlanService, strategyFactory, requestLogService, requestMetricsService));
        registration.addUrlPatterns("/hello", "/api/*"); // Rate limiter applies to these endpoints
        registration.setName("RateLimiterMiddleware");
        registration.setOrder(1); // Ensure it's one of the earliest filters
        return registration;
    }

    // Optional: Add RedisConnectionFactory explicitly if needed later
    // Spring Boot autoconfigures RedisConnectionFactory via application.properties
}