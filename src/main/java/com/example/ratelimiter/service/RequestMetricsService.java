package com.example.ratelimiter.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class RequestMetricsService {

    private final MeterRegistry meterRegistry;

    public RequestMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void logRequest(String userId, boolean allowed) {
        meterRegistry.counter("ratelimiter.requests.total", "user", userId).increment();

        if (allowed) {
            meterRegistry.counter("ratelimiter.requests.allowed", "user", userId).increment();
        } else {
            meterRegistry.counter("ratelimiter.requests.blocked", "user", userId).increment();
        }
    }
}
