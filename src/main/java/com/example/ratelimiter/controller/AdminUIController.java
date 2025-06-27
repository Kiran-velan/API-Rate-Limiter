package com.example.ratelimiter.controller;

import com.example.ratelimiter.model.PlanType;
import com.example.ratelimiter.model.UserPlan;
import com.example.ratelimiter.service.UserPlanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class AdminUIController {

    private final UserPlanService userPlanService;

    public AdminUIController(UserPlanService userPlanService) {
        this.userPlanService = userPlanService;
    }

    @GetMapping
    public String viewDashboard(Model model) {
        // Convert Map<String, PlanType> to List<UserPlan> for template usage
        List<UserPlan> userList = userPlanService.getAllPlans().entrySet().stream()
                .map(entry -> new UserPlan(entry.getKey(), entry.getValue()))
                .toList();

        model.addAttribute("users", userList);
        model.addAttribute("plans", PlanType.values());
        return "dashboard";
    }

    @PostMapping("/update")
    public String updateUserPlan(@RequestParam String userId, @RequestParam PlanType plan) {
        if (userId != null && !userId.isBlank()) {
            userPlanService.updateUserPlan(userId, plan);
            System.out.println("✅ Updated plan for " + userId + " → " + plan);
        } else {
            System.out.println("⚠️ Attempted to update user with blank userId.");
        }
        return "redirect:/dashboard";
    }

//    @GetMapping
//    public String viewDashboard(Model model) {
//        model.addAttribute("users", userPlanService.getAllPlans());
//        model.addAttribute("plans", PlanType.values());
//        return "dashboard";
//    }

//    @PostMapping("/update")
//    public String updateUserPlan(@RequestParam String userId, @RequestParam PlanType plan) {
//        userPlanService.updateUserPlan(userId, plan);
//        return "redirect:/dashboard";
//    }
}
