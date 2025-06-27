package com.example.ratelimiter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RequestMetricsService {

    private final Map<String, TreeMap<String, Integer>> requestTimeline = new ConcurrentHashMap<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    private final UserPlanService userPlanService;
    private final RequestLogService requestLogService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public RequestMetricsService(UserPlanService userPlanService,
                                 RequestLogService requestLogService) {
        this.requestLogService = requestLogService;
        this.userPlanService = userPlanService;
    }

    public void logRequest(String userId, boolean allowed) {
        // 1. Store in Redis log
        requestLogService.logRequest(userId, allowed);

        // 2. Track per-minute timeline in Redis
        String currentMinute = LocalDateTime.now().format(formatter);
        String key = "timeline:" + userId;
        String field = currentMinute;
        redisTemplate.opsForHash().increment(key, field, 1);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
    }

    public Map<String, Object> getChartData() {
        Map<String, Object> data = new HashMap<>();
        data.put("lineChart", getLineChartData());
        data.put("barChart", getUserRequestBarChart());
        data.put("pieChart", getUserPlanDistributionPieChart());

        return data;
    }

    private Map<String, Object> getLineChartData() {
        TreeMap<String, Integer> timeline = new TreeMap<>();

        Set<String> userIds = userPlanService.getAllPlans().keySet();
        for (String userId : userIds) {
            String key = "timeline:" + userId;
            Map<Object, Object> redisData = redisTemplate.opsForHash().entries(key);

            for (Map.Entry<Object, Object> entry : redisData.entrySet()) {
                String time = entry.getKey().toString();
                int count = Integer.parseInt(entry.getValue().toString());
                timeline.merge(time, count, Integer::sum);
            }
        }

        List<String> labels = new ArrayList<>(timeline.keySet());
        List<Integer> values = new ArrayList<>(timeline.values());

        return Map.of(
                "labels", labels,
                "datasets", List.of(Map.of(
                        "label", "Requests Over Time",
                        "data", values,
                        "fill", false,
                        "borderColor", "blue",
                        "tension", 0.1
                ))
        );
    }

    private Map<String, Object> getUserRequestBarChart() {
        List<String> labels = new ArrayList<>();
        List<Double> allowedData = new ArrayList<>();
        List<Double> blockedData = new ArrayList<>();

        Map<String, List<RequestLogService.LogEntry>> logs = requestLogService.getAllLogs();

        for (Map.Entry<String, List<RequestLogService.LogEntry>> entry : logs.entrySet()) {
            String user = entry.getKey();
            labels.add(user);

            long allowed = entry.getValue().stream().filter(e -> e.allowed).count();
            long blocked = entry.getValue().size() - allowed;

            allowedData.add((double) allowed);
            blockedData.add((double) blocked);
        }

        return Map.of(
                "labels", labels,
                "datasets", List.of(
                        Map.of("label", "Allowed", "data", allowedData, "backgroundColor", "green"),
                        Map.of("label", "Blocked", "data", blockedData, "backgroundColor", "red")
                )
        );
    }

    private Map<String, Object> getUserPlanDistributionPieChart() {
        Map<String, Long> planCounts = userPlanService.getAllPlans().values().stream()
                .collect(Collectors.groupingBy(Enum::name, Collectors.counting()));

        List<String> labels = new ArrayList<>(planCounts.keySet());
        List<Long> values = new ArrayList<>(planCounts.values());

        Map<String, Object> pie = new HashMap<>();
        pie.put("labels", labels);
        pie.put("datasets", List.of(
                Map.of("label", "User Plan Distribution", "data", values,
                        "backgroundColor", List.of("cyan", "orange", "purple", "green", "red"))
        ));
        return pie;
    }
}
