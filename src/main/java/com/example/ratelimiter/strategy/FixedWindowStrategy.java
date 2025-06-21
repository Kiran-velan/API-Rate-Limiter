package com.example.ratelimiter.strategy;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowStrategy implements RateLimitingStrategy {

    private final int maxRequests;
    private final long windowInSeconds;

    private final Map<String, UserRequestData> userRequestMap = new ConcurrentHashMap<>();

    public FixedWindowStrategy(int maxRequests, long windowInSeconds) {
        this.maxRequests = maxRequests;
        this.windowInSeconds = windowInSeconds;
    }

    @Override
    public boolean allowRequest(String userId) {
        long currentWindow = Instant.now().getEpochSecond() / windowInSeconds;

        userRequestMap.putIfAbsent(userId, new UserRequestData(currentWindow, 0));

        UserRequestData data = userRequestMap.get(userId);
        if (data.window != currentWindow) {
            data.window = currentWindow;
            data.count = 1;
            return true;
        }

        if (data.count < maxRequests) {
            data.count++;
            return true;
        }

        return false;
    }

    private static class UserRequestData {
        long window;
        int count;

        public UserRequestData(long window, int count) {
            this.window = window;
            this.count = count;
        }
    }
}
