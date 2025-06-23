package com.example.ratelimiter.model;

public enum PlanType {
    FREE(5, 10, "FIXED_WINDOW"),
    PRO(100, 60, "TOKEN_BUCKET");

    private final int limit;
    private final int windowInSeconds;
    private final String strategy;

    PlanType(int limit, int windowInSeconds, String strategy) {
        this.limit = limit;
        this.windowInSeconds = windowInSeconds;
        this.strategy = strategy;
    }

    public int getLimit() {
        return limit;
    }

    public int getWindowInSeconds() {
        return windowInSeconds;
    }

    public String getStrategy() {
        return strategy;
    }
}
