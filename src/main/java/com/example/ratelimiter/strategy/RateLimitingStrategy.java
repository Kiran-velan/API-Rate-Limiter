package com.example.ratelimiter.strategy;

import com.example.ratelimiter.model.UserPlan;

// In interfaces, all methods are public and abstract by default
public interface  RateLimitingStrategy {
    boolean allowRequest(String userId, UserPlan plan);
}
