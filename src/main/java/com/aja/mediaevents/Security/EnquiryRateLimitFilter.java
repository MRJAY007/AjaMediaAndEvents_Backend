
package com.aja.mediaevents.Security;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EnquiryRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 5;
    private static final long TIME_WINDOW = 60_000;

    private final ConcurrentHashMap<String, RateLimitEntry> requests =
            new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        if (!HttpMethod.POST.matches(request.getMethod())
                || !request.getRequestURI().equals(
                        request.getContextPath() + "/api/enquiries")) {

            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        RateLimitEntry entry = requests.computeIfAbsent(
                clientIp,
                ip -> new RateLimitEntry(currentTime)
        );

        synchronized (entry) {

            if (currentTime - entry.windowStart >= TIME_WINDOW) {
                entry.windowStart = currentTime;
                entry.count = 0;
            }

            if (entry.count >= MAX_REQUESTS) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Retry-After", "60");

                response.getWriter().write(
                        "{\"message\":\"Too many enquiries. Please try again later.\"}"
                );
                return;
            }

            entry.count++;
        }

        filterChain.doFilter(request, response);
    }

    private static class RateLimitEntry {
        private long windowStart;
        private int count;

        private RateLimitEntry(long windowStart) {
            this.windowStart = windowStart;
        }
    }
}
