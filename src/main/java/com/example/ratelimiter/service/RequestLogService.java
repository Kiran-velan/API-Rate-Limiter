package com.example.ratelimiter.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class RequestLogService {

    public static class LogEntry {
        public final long timestamp;
        public final boolean allowed;

        public LogEntry(boolean allowed) {
            this.timestamp = Instant.now().getEpochSecond();
            this.allowed = allowed;
        }
    }

    private final Map<String, List<LogEntry>> requestLogs = new HashMap<>();
    private final Map<String, Integer> blockCounts = new HashMap<>();

    public void logRequest(String userId, boolean allowed) {
        requestLogs.computeIfAbsent(userId, k -> new ArrayList<>())
                .add(new LogEntry(allowed));

        if (!allowed) {
            blockCounts.put(userId, blockCounts.getOrDefault(userId, 0) + 1);
        }
    }

    public List<LogEntry> getRequestLog(String userId) {
        return requestLogs.getOrDefault(userId, Collections.emptyList());
    }

    public int getBlockCount(String userId) {
        return blockCounts.getOrDefault(userId, 0);
    }

    public Map<String, List<LogEntry>> getAllLogs() {
        return requestLogs;
    }
}
