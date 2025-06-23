package com.example.ratelimiter.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPlan {
    private String userId;
    private PlanType plan;

    public UserPlan(String userId, PlanType plan) {
        this.userId = userId;
        this.plan = plan;
    }

    // Convenience getters for limits and strategy
    public int getLimit() {
        return plan.getLimit();
    }

    public int getWindowInSeconds() {
        return plan.getWindowInSeconds();
    }

    public String getStrategy() {
        return plan.getStrategy();
    }

    public String getPlanName() {
        return plan.name();
    }
}
