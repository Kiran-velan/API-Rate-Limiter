package com.example.ratelimiter.controller;

import com.example.ratelimiter.model.PlanType;
import com.example.ratelimiter.model.UserPlan;
import com.example.ratelimiter.service.RequestLogService;
import com.example.ratelimiter.service.UserPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserPlanService userPlanService;
    private final RequestLogService requestLogService;

    public AdminController(UserPlanService userPlanService, RequestLogService requestLogService) {
        this.userPlanService = userPlanService;
        this.requestLogService = requestLogService;
    }

    @GetMapping("/users")
    public List<UserPlan> getAllUsers() {
        return userPlanService.getAllPlans();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserPlan> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userPlanService.getPlanForUser(userId));
    }

    @PostMapping("/users")
    public ResponseEntity<String> addOrUpdateUser(@RequestParam String userId, @RequestParam PlanType plan) {
        userPlanService.updateUserPlan(userId, plan);
        return ResponseEntity.ok("User " + userId + " assigned to " + plan + " plan.");
    }

    @GetMapping("/metrics/{userId}")
    public Map<String, Object> getUserMetrics(@PathVariable String userId) {
        return Map.of(
                "totalRequests", requestLogService.getRequestLog(userId).size(),
                "blockedRequests", requestLogService.getBlockCount(userId),
                "log", requestLogService.getRequestLog(userId)
        );
    }

    @GetMapping("/metrics")
    public Map<String, List<RequestLogService.LogEntry>> getAllUserLogs() {
        return requestLogService.getAllLogs();
    }

}
