package com.example.ratelimiter.service;

import com.example.ratelimiter.model.PlanType;
import com.example.ratelimiter.model.UserPlan;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserPlanService {

    private final Map<String, UserPlan> userPlanMap = new HashMap<>();

    public UserPlanService() {
        // Assign users to plans
        userPlanMap.put("user123", new UserPlan("user123", PlanType.FREE));
        userPlanMap.put("user999", new UserPlan("user999", PlanType.PRO));
    }

    public UserPlan getPlanForUser(String userId) {
        UserPlan plan = userPlanMap.get(userId);
        if (plan == null) {
            System.out.println("User " + userId + " not found. Using Default FREE plan.");
            return new UserPlan(userId, PlanType.FREE);
        }
        return plan;
    }
}
