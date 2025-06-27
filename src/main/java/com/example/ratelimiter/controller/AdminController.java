package com.example.ratelimiter.controller;

import com.example.ratelimiter.model.PlanType;
import com.example.ratelimiter.model.UserPlan;
import com.example.ratelimiter.service.RequestLogService;
import com.example.ratelimiter.service.RequestMetricsService;
import com.example.ratelimiter.service.UserPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserPlanService userPlanService;
    private final RequestLogService requestLogService;
    private final RequestMetricsService requestMetricsService;

    public AdminController(UserPlanService userPlanService,
                           RequestLogService requestLogService,
                           RequestMetricsService requestMetricsService) {
        this.userPlanService = userPlanService;
        this.requestLogService = requestLogService;
        this.requestMetricsService = requestMetricsService;
    }

//    @GetMapping("/users")
//    @ResponseBody
//    public List<UserPlan> getAllUsers() {
//        return userPlanService.getAllPlans();
//    }


    @GetMapping("/home")
    public String adminHome() {
        return "admin-home";
    }

    @GetMapping("/users")
    @ResponseBody
    public List<UserPlan> getAllUsers() {
        return userPlanService.getAllPlans().entrySet().stream()
                .map(entry -> new UserPlan(entry.getKey(), entry.getValue()))
                .toList();
    }

    @GetMapping("/users/{userId}")
    @ResponseBody
    public ResponseEntity<UserPlan> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userPlanService.getPlanForUser(userId));
    }

    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<String> addOrUpdateUser(@RequestParam String userId, @RequestParam PlanType plan) {
        userPlanService.updateUserPlan(userId, plan);
        return ResponseEntity.ok("User " + userId + " assigned to " + plan + " plan.");
    }

    @GetMapping("/metrics/{userId}")
    @ResponseBody
    public Map<String, Object> getUserMetrics(@PathVariable String userId) {
        return Map.of(
                "totalRequests", requestLogService.getRequestLog(userId).size(),
                "blockedRequests", requestLogService.getBlockCount(userId),
                "log", requestLogService.getRequestLog(userId)
        );
    }

    @GetMapping("/metrics")
    @ResponseBody
    public Map<String, List<RequestLogService.LogEntry>> getAllUserLogs() {
        return requestLogService.getAllLogs();
    }

    @GetMapping("/logs")
    public String showRequestLogs(org.springframework.ui.Model model) {
        Map<String, List<RequestLogService.LogEntry>> logs = requestLogService.getAllLogs();
        model.addAttribute("logs", logs);
        model.addAttribute("userIds", logs.keySet());
        return "logs"; // logs.html from templates
    }

    @GetMapping("/chart")
    public String showChartPage() {
        return "chart"; // chart.html from templates
    }

    @GetMapping("/chart/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChartData() {
        return ResponseEntity.ok(requestMetricsService.getChartData());
    }

}
