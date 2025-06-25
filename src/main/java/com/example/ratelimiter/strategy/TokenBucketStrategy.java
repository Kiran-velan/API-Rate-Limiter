package com.example.ratelimiter.strategy;

import com.example.ratelimiter.model.UserPlan;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketStrategy implements RateLimitingStrategy {

    private static class TokenBucket {
        double tokens;
        long lastRefillTimestamp;

        public TokenBucket(double tokens, long lastRefillTimestamp) {
            this.tokens = tokens;
            this.lastRefillTimestamp = lastRefillTimestamp;
        }
    }

    private final Map<String, TokenBucket> bucketMap = new ConcurrentHashMap<>();

    @Override
    public boolean allowRequest(String userId, UserPlan plan) {
        long currentTime = Instant.now().getEpochSecond();
        bucketMap.putIfAbsent(userId, new TokenBucket(plan.getLimit(), currentTime));

        TokenBucket bucket = bucketMap.get(userId);
        double tokensToAdd = (double) (currentTime - bucket.lastRefillTimestamp) * plan.getLimit() / plan.getWindowInSeconds();
        bucket.tokens = Math.min(plan.getLimit(), bucket.tokens + tokensToAdd);
        bucket.lastRefillTimestamp = currentTime;

        if (bucket.tokens >= 1) {
            bucket.tokens -= 1;
            return true;
        }

        return false;
    }
}
