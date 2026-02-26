package com.literatememory.linktree.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private static final int LIMIT = 120;
    private static final long WINDOW_SECONDS = 60;

    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.startsWith("/events/") || path.startsWith("/p/") || path.startsWith("/safety/"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String key = request.getRemoteAddr() + "|" + request.getRequestURI();
        Instant now = Instant.now();

        Counter counter = counters.compute(key, (k, existing) -> {
            if (existing == null || now.isAfter(existing.windowStart.plusSeconds(WINDOW_SECONDS))) {
                return new Counter(now, 1);
            }
            existing.count++;
            return existing;
        });

        if (counter.count > LIMIT) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Rate limit exceeded\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static class Counter {
        private final Instant windowStart;
        private int count;

        private Counter(Instant windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
