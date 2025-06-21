package com.example.ratelimiter.middleware;

import com.example.ratelimiter.strategy.RateLimitingStrategy;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Component
public class RateLimiterMiddleware implements Filter {

    private final RateLimitingStrategy strategy;

    public RateLimiterMiddleware(RateLimitingStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        String userId = httpReq.getHeader("X-User-Id");
        if (userId == null || userId.isEmpty()) {
            httpRes.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpRes.getWriter().write("Missing X-User-Id header");
            return;
        }

        boolean allowed = strategy.allowRequest(userId);
        System.out.println("User: " + userId + " -> Allowed: " + allowed);
        if (!allowed) {
            //  httpRes.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS); -- this is not working, servlet omits this status
            httpRes.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpRes.getWriter().write("Rate limit exceeded");
            return;
        }

        chain.doFilter(request, response);
    }
}
