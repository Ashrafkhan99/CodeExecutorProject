package com.coderank.executor.web;

import com.coderank.executor.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties props;
    private final ObjectMapper mapper = new ObjectMapper();

    public RateLimitFilter(RateLimitProperties props) {
        this.props = props;
    }

    private static final class Counter {
        volatile long windowStartEpochMinute;
        final AtomicInteger count = new AtomicInteger(0);
    }

    private final ConcurrentHashMap<String, Counter> counters = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // limit only POST /api/execute
        return !("POST".equalsIgnoreCase(request.getMethod()) && "/api/execute".equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // Capture client IP for rate-limit key (handles simple X-Forwarded-For)
        String ip = req.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            int comma = ip.indexOf(',');
            if (comma > 0) ip = ip.substring(0, comma).trim();
        } else {
            ip = req.getRemoteAddr();
        }
        RequestIpHolder.set(ip);
        try {
            int limit = props.getExecute().getPerMinute();
            if (limit <= 0) { // disabled
                chain.doFilter(req, res);
                return;
            }

            String key = resolveKey();
            long nowMin = Instant.now().getEpochSecond() / 60;
            Counter c = counters.computeIfAbsent(key, k -> {
                Counter nc = new Counter();
                nc.windowStartEpochMinute = nowMin;
                return nc;
            });

            synchronized (c) {
                if (c.windowStartEpochMinute != nowMin) {
                    c.windowStartEpochMinute = nowMin;
                    c.count.set(0);
                }
                int used = c.count.incrementAndGet();
                int remaining = Math.max(0, limit - used);
                res.setHeader("X-RateLimit-Limit", String.valueOf(limit));
                res.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
                res.setHeader("X-RateLimit-Window", "60"); // seconds

                if (used > limit) {
                    res.setStatus(429);
                    long resetSec = 60 - (Instant.now().getEpochSecond() % 60);
                    res.setHeader("Retry-After", String.valueOf(resetSec));
                    res.setContentType("application/json");
                    mapper.writeValue(res.getOutputStream(), Map.of(
                            "error", "too_many_requests",
                            "message", "Rate limit exceeded. Try again later.",
                            "limit", limit,
                            "remaining", 0,
                            "resetSeconds", resetSec
                    ));
                    return;
                }
            }

            chain.doFilter(req, res);
        } finally {
            RequestIpHolder.set(null);
        }
    }

    private String resolveKey() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User u && u.getId() != null) {
            return "u:" + u.getId();
        }
        return "ip:" + Objects.toString(RequestIpHolder.getClientIp(), "unknown");
    }

    /** Minimal ThreadLocal IP holder */
    private static final class RequestIpHolder {
        private static final ThreadLocal<String> IP = new ThreadLocal<>();
        static void set(String ip) { IP.set(ip); }
        static String getClientIp() { return IP.get(); }
    }
}
