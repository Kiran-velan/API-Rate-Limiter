package com.example.ratelimiter.strategy;

import com.example.ratelimiter.model.UserPlan;

public class TokenBucketStrategy implements RateLimitingStrategy {
    @Override
    public boolean allowRequest(String userId, UserPlan plan) {
        // Always allow for now
        return true;
    }
}
