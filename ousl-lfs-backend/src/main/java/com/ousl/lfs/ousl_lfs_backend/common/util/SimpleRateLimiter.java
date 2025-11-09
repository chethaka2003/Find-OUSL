package com.ousl.lfs.ousl_lfs_backend.common.util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimpleRateLimiter {
    private final int maxPerHour;
    private final Map<String, Deque<Instant>> bucket = new ConcurrentHashMap<>();

    public SimpleRateLimiter(@Value("${ulfs.auth.rateLimitPerIpPerHour:20}") int maxPerHour) {
        this.maxPerHour = maxPerHour;
    }
    public void check(String key) {
        var now = Instant.now();
        var q = bucket.computeIfAbsent(key, k -> new ArrayDeque<>());
        while (!q.isEmpty() && Duration.between(q.peekFirst(), now).toHours() >= 1) q.pollFirst();
        if (q.size() >= maxPerHour) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many attempts; try later");
        }
        q.addLast(now);
    }
}