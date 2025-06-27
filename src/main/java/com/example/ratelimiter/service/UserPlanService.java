package com.example.ratelimiter.service;

import com.example.ratelimiter.model.PlanType;
import com.example.ratelimiter.model.UserPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserPlanService {

    private final Map<String, UserPlan> userPlanMap = new HashMap<>();
    private static final String REDIS_KEY = "user:plans";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Below is for in-memory implementation
//    public UserPlanService() {
//        // Assign users to plans
//        userPlanMap.put("user123", new UserPlan("user123", PlanType.FREE));
//        userPlanMap.put("user999", new UserPlan("user999", PlanType.PRO));
//    }
//
//    public UserPlan getPlanForUser(String userId) {
//        return userPlanMap.getOrDefault(userId, new UserPlan(userId, PlanType.FREE));
//    }
//
//    public List<UserPlan> getAllPlans() {
//        return new ArrayList<>(userPlanMap.values());
//    }
//
//    public void updateUserPlan(String userId, PlanType plan) {
//        userPlanMap.put(userId, new UserPlan(userId, plan));
//    }


    // Below is for Redis implementation
    // Get plan for a user, defaulting to FREE if not set
    public UserPlan getPlanForUser(String userId) {
        String planStr = (String) redisTemplate.opsForHash().get(REDIS_KEY, userId);
        PlanType plan = planStr != null ? PlanType.valueOf(planStr.toUpperCase()) : PlanType.FREE;
        return new UserPlan(userId, plan);
    }

    // Update a user's plan (e.g., from admin dashboard)
    public void updateUserPlan(String userId, PlanType plan) {
        redisTemplate.opsForHash().put(REDIS_KEY, userId, plan.name());
    }

    // Get all userId → plan mappings
    public Map<String, PlanType> getAllPlans() {
        Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(REDIS_KEY);
        Map<String, PlanType> plans = new HashMap<>();
        for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
            String userId = entry.getKey().toString();
            String planStr = entry.getValue().toString();
            plans.put(userId, PlanType.valueOf(planStr.toUpperCase()));
        }
        return plans;
    }

    // Set default plan (used in middleware/logging if user is new)
    public void ensureUserHasPlan(String userId) {
        if (!redisTemplate.opsForHash().hasKey(REDIS_KEY, userId)) {
            redisTemplate.opsForHash().put(REDIS_KEY, userId, PlanType.FREE.name());
        }
    }

}
