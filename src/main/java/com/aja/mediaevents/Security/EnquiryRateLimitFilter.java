
package com.aja.mediaevents.Security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

public class EnquiryRateLimitFilter extends OncePerRequestFilter {

    private static final int ENQUIRY_LIMIT = 5;
    private static final long ENQUIRY_WINDOW = 60_000L;

    private static final int TRAFFIC_LIMIT = 60;
    private static final long TRAFFIC_WINDOW = 60_000L;

    private static final int LOGIN_EMAIL_LIMIT = 5;
    private static final int LOGIN_IP_LIMIT = 20;
    private static final long LOGIN_WINDOW = 15 * 60_000L;

    private static final int MAX_LOGIN_BODY_SIZE = 4096;
    private static final int MAX_ENTRIES = 50_000;

    private final ConcurrentHashMap<String, RateLimitEntry> requests =
            new ConcurrentHashMap<>();

    private final AtomicInteger cleanupCounter = new AtomicInteger();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        String contextPath = request.getContextPath();
        String clientIp = request.getRemoteAddr();

        // Public enquiry: 5 requests per IP per minute.
        if (HttpMethod.POST.matches(method)
                && path.equals(contextPath + "/api/enquiries")) {

            if (isLimited(
                    "enquiry:" + clientIp,
                    ENQUIRY_LIMIT,
                    ENQUIRY_WINDOW)) {

                writeRateLimitResponse(
                        response, 60,
                        "Too many enquiries. Please try again later."
                );
                return;
            }

            filterChain.doFilter(request, response);
            return;
        }

        // Public traffic tracking: 60 requests per IP per minute.
        if (HttpMethod.POST.matches(method)
                && path.equals(contextPath + "/api/traffic/track")) {

            if (isLimited(
                    "traffic:" + clientIp,
                    TRAFFIC_LIMIT,
                    TRAFFIC_WINDOW)) {

                writeRateLimitResponse(
                        response, 60,
                        "Too many tracking requests. Please try again later."
                );
                return;
            }

            filterChain.doFilter(request, response);
            return;
        }

        // Admin login: check the IP limit before reading the body.
        if (HttpMethod.POST.matches(method)
                && path.equals(contextPath + "/api/admin/login")) {

            if (isLimited(
                    "login-ip:" + clientIp,
                    LOGIN_IP_LIMIT,
                    LOGIN_WINDOW)) {

                writeRateLimitResponse(
                        response, 900,
                        "Too many login attempts. Please try again later."
                );
                return;
            }

            // Reject an oversized body using Content-Length when available.
            if (request.getContentLengthLong() > MAX_LOGIN_BODY_SIZE) {
                writePayloadTooLarge(response);
                return;
            }

            // Also cap chunked or unknown-length requests.
            byte[] body = request.getInputStream()
                    .readNBytes(MAX_LOGIN_BODY_SIZE + 1);

            if (body.length > MAX_LOGIN_BODY_SIZE) {
                writePayloadTooLarge(response);
                return;
            }

            String email = extractEmail(body);

            if (email != null && !email.isBlank()) {

                String key = "login-email:"
                        + clientIp + ":"
                        + email.trim().toLowerCase();

                if (isLimited(key, LOGIN_EMAIL_LIMIT, LOGIN_WINDOW)) {
                    writeRateLimitResponse(
                            response, 900,
                            "Too many login attempts. Please try again later."
                    );
                    return;
                }
            }

            CachedBodyRequest wrappedRequest =
                    new CachedBodyRequest(request, body);

            filterChain.doFilter(wrappedRequest, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractEmail(byte[] body) {

        try {
            Map<?, ?> data = objectMapper.readValue(body, Map.class);
            Object email = data.get("email");

            return email == null ? null : email.toString();

        } catch (Exception ex) {
            return null;
        }
    }

    private boolean isLimited(
            String key,
            int maximumRequests,
            long window) {

        long now = System.currentTimeMillis();

        RateLimitEntry entry = requests.get(key);

        if (entry == null && requests.size() >= MAX_ENTRIES) {

            requests.entrySet().removeIf(item -> {
                RateLimitEntry oldEntry = item.getValue();

                synchronized (oldEntry) {
                    return now - oldEntry.windowStart > LOGIN_WINDOW;
                }
            });

            // Fail closed for a new key if the map is still full.
            if (requests.size() >= MAX_ENTRIES) {
                return true;
            }
        }

        entry = requests.computeIfAbsent(
                key,
                ignored -> new RateLimitEntry(now)
        );

        synchronized (entry) {

            if (now - entry.windowStart >= window) {
                entry.windowStart = now;
                entry.count = 0;
            }

            if (entry.count >= maximumRequests) {
                return true;
            }

            entry.count++;
        }

        cleanupExpiredEntries(now);
        return false;
    }

    private void cleanupExpiredEntries(long now) {

        if (cleanupCounter.incrementAndGet() % 256 != 0) {
            return;
        }

        requests.entrySet().removeIf(item -> {
            RateLimitEntry entry = item.getValue();

            synchronized (entry) {
                return now - entry.windowStart > LOGIN_WINDOW;
            }
        });
    }

    private void writeRateLimitResponse(
            HttpServletResponse response,
            int retryAfter,
            String message) throws IOException {

        response.setStatus(HttpServletResponse.SC_REQUEST_URI_TOO_LONG);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Retry-After", String.valueOf(retryAfter));

        response.getWriter().write(
                objectMapper.writeValueAsString(
                        Map.of("message", message)
                )
        );
    }

    private void writePayloadTooLarge(
            HttpServletResponse response) throws IOException {

        response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                objectMapper.writeValueAsString(
                        Map.of("message", "Login request body is too large")
                )
        );
    }

    private static class RateLimitEntry {

        private long windowStart;
        private int count;

        private RateLimitEntry(long windowStart) {
            this.windowStart = windowStart;
            this.count = 0;
        }
    }

    private static class CachedBodyRequest
            extends HttpServletRequestWrapper {

        private final byte[] body;

        private CachedBodyRequest(
                HttpServletRequest request,
                byte[] body) {

            super(request);
            this.body = body;
        }

        @Override
        public ServletInputStream getInputStream() {

            ByteArrayInputStream input =
                    new ByteArrayInputStream(body);

            return new ServletInputStream() {

                @Override
                public int read() {
                    return input.read();
                }

                @Override
                public boolean isFinished() {
                    return input.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public BufferedReader getReader() {

            return new BufferedReader(
                    new InputStreamReader(
                            getInputStream(),
                            StandardCharsets.UTF_8
                    )
            );
        }
    }
}
