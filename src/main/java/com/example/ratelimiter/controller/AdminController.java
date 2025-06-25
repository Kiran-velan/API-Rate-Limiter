package com.example.ratelimiter.controller;

import com.example.ratelimiter.model.PlanType;
import com.example.ratelimiter.model.UserPlan;
import com.example.ratelimiter.service.UserPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserPlanService userPlanService;

    public AdminController(UserPlanService userPlanService) {
        this.userPlanService = userPlanService;
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
}
