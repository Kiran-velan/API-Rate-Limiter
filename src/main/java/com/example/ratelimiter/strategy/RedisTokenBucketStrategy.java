package com.example.ratelimiter.strategy;

import com.example.ratelimiter.model.UserPlan;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class RedisTokenBucketStrategy implements RateLimitingStrategy {

    private final StringRedisTemplate redisTemplate;

    public RedisTokenBucketStrategy(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean allowRequest(String userId, UserPlan plan) {
        String tokensKey = "tokenbucket:" + userId + ":tokens";
        String timestampKey = "tokenbucket:" + userId + ":lastRefill";

        int maxTokens = plan.getLimit(); // Maximum capacity of the bucket
        int refillRate = plan.getLimit(); // Tokens added per window
        int windowSeconds = plan.getWindowInSeconds(); // Time interval for full refill

        long now = Instant.now().getEpochSecond();
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        // Fetch current token count and last refill time
        String tokensStr = ops.get(tokensKey);
        String lastRefillStr = ops.get(timestampKey);

        // First-time initialization
        if (tokensStr == null || lastRefillStr == null) {
            ops.set(tokensKey, String.valueOf(maxTokens - 1), windowSeconds * 2L, TimeUnit.SECONDS);
            ops.set(timestampKey, String.valueOf(now), windowSeconds * 2L, TimeUnit.SECONDS);
            return true;
        }

        int tokens = Integer.parseInt(tokensStr);
        long lastRefill = Long.parseLong(lastRefillStr);

        // Refill logic
        long elapsed = now - lastRefill;
        int tokensToAdd = (int) ((elapsed * refillRate) / windowSeconds);

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + tokensToAdd);
            lastRefill = now;

            ops.set(tokensKey, String.valueOf(tokens), windowSeconds * 2L, TimeUnit.SECONDS);
            ops.set(timestampKey, String.valueOf(lastRefill), windowSeconds * 2L, TimeUnit.SECONDS);
        }

        // Consume a token if available
        if (tokens > 0) {
            tokens -= 1;
            ops.set(tokensKey, String.valueOf(tokens), windowSeconds * 2L, TimeUnit.SECONDS);
            return true;
        } else {
            return false;
        }
    }
}