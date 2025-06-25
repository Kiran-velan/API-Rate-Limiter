package com.example.ratelimiter.controller;

import com.example.ratelimiter.model.PlanType;
import com.example.ratelimiter.model.UserPlan;
import com.example.ratelimiter.service.UserPlanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class AdminUIController {

    private final UserPlanService userPlanService;

    public AdminUIController(UserPlanService userPlanService) {
        this.userPlanService = userPlanService;
    }

    @GetMapping
    public String viewDashboard(Model model) {
        model.addAttribute("users", userPlanService.getAllPlans());
        model.addAttribute("plans", PlanType.values());
        return "dashboard";
    }

    @PostMapping("/update")
    public String updateUserPlan(@RequestParam String userId, @RequestParam PlanType plan) {
        userPlanService.updateUserPlan(userId, plan);
        return "redirect:/dashboard";
    }
}
