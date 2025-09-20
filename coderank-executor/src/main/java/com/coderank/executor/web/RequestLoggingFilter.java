package com.coderank.executor.web;

import com.coderank.executor.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        long start = System.nanoTime();
        try {
            chain.doFilter(req, res);
        } finally {
            long ms = Math.round((System.nanoTime() - start) / 1_000_000.0);
            String method = req.getMethod();
            String path = req.getRequestURI();
            int status = res.getStatus();
            String ip = clientIp(req);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = (auth != null && auth.getPrincipal() instanceof User u) ? u.getEmail() : "-";
            // enrich MDC for log pattern
            MDC.put("userEmail", userEmail);
            try {
                String limit = res.getHeader("X-RateLimit-Limit");
                String remain = res.getHeader("X-RateLimit-Remaining");
                log.info("{} {} -> {} {}ms ip={} rate={}/{}", method, path, status, ms, ip,
                        (limit == null ? "-" : limit), (remain == null ? "-" : remain));
            } finally {
                MDC.remove("userEmail");
            }
        }
    }

    private static String clientIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            int comma = ip.indexOf(',');
            if (comma > 0) ip = ip.substring(0, comma).trim();
            return ip;
        }
        return req.getRemoteAddr();
    }
}
