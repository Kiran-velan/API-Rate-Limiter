package com.example.ratelimiter.middleware;

import com.example.ratelimiter.model.UserPlan;
import com.example.ratelimiter.service.RequestLogService;
import com.example.ratelimiter.service.RequestMetricsService;
import com.example.ratelimiter.service.UserPlanService;
import com.example.ratelimiter.strategy.RateLimitingStrategy;
import com.example.ratelimiter.strategy.StrategyFactory;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;

import java.io.IOException;

//  removed @Component annotation Since we're now registering the filter manually in Configuration
//@Component
public class RateLimiterMiddleware implements Filter {

    private final UserPlanService userPlanService;
    private final StrategyFactory strategyFactory;
    private final RequestLogService requestLogService;
    private final RequestMetricsService requestMetricsService;

    public RateLimiterMiddleware(UserPlanService userPlanService,
                                 StrategyFactory strategyFactory,
                                 RequestLogService requestLogService,
                                 RequestMetricsService requestMetricsService) {
        this.userPlanService = userPlanService;
        this.strategyFactory = strategyFactory;
        this.requestLogService = requestLogService;
        this.requestMetricsService = requestMetricsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        String userId = httpReq.getHeader("X-User-Id");
        UserPlan plan = userPlanService.getPlanForUser(userId);
        RateLimitingStrategy strategy = strategyFactory.getStrategy(plan.getStrategy());

        if (userId == null || userId.isEmpty()) {
            httpRes.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpRes.getWriter().write("Missing X-User-Id header");
            return;
        }

        boolean allowed = strategy.allowRequest(userId, plan);
        requestLogService.logRequest(userId, allowed);
        requestMetricsService.logRequest(userId, allowed);
        System.out.println("User: " + userId + " | Plan: " + plan.getPlanName() + " | Allowed: " + allowed);
        if (!allowed) {
            //  httpRes.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS); -- this is not working, servlet omits this status
            httpRes.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpRes.getWriter().write("Rate limit exceeded");
            return;
        }

        chain.doFilter(request, response);
    }
}
