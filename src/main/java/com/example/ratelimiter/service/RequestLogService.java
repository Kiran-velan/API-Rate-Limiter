package com.example.ratelimiter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequestLogService {

    private static final String PREFIX = "request:logs:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public static class LogEntry {
        public final long timestamp;
        public final boolean allowed;

        public LogEntry(long timestamp, boolean allowed) {
            this.timestamp = timestamp;
            this.allowed = allowed;
        }

        public LogEntry(boolean allowed) {
            this.timestamp = Instant.now().getEpochSecond();
            this.allowed = allowed;
        }

        public static LogEntry fromString(String s) {
            String[] parts = s.split("\\|");
            return new LogEntry(Long.parseLong(parts[0]), Boolean.parseBoolean(parts[1]));
        }

        @Override
        public String toString() {
            return timestamp + "|" + allowed;
        }
    }

    public void logRequest(String userId, boolean allowed) {
        String redisKey = PREFIX + userId;
        LogEntry entry = new LogEntry(allowed);
        redisTemplate.opsForList().rightPush(redisKey, entry.toString());

        // Optional: keep only last N entries (e.g., 100)
        redisTemplate.opsForList().trim(redisKey, -100, -1);
    }

    public List<LogEntry> getRequestLog(String userId) {
        List<String> rawLogs = redisTemplate.opsForList().range(PREFIX + userId, 0, -1);
        if (rawLogs == null) return Collections.emptyList();

        return rawLogs.stream().map(LogEntry::fromString).collect(Collectors.toList());
    }

    public int getBlockCount(String userId) {
        return (int) getRequestLog(userId).stream().filter(log -> !log.allowed).count();
    }

    public Map<String, List<LogEntry>> getAllLogs() {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys == null) return Collections.emptyMap();

        Map<String, List<LogEntry>> all = new HashMap<>();
        for (String key : keys) {
            String userId = key.substring(PREFIX.length());
            all.put(userId, getRequestLog(userId));
        }
        return all;
    }
}