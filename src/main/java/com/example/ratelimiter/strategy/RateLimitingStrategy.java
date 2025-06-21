package com.example.ratelimiter.strategy;

// In interfaces, all methods are public and abstract by default
public interface  RateLimitingStrategy {
    boolean allowRequest(String userId);
}
