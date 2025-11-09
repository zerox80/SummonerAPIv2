package com.zerox80.riotapi.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final class Window {
        volatile long startMs;
        volatile int count;

        Window(long startMs, int count) {
            this.startMs = startMs;
            this.count = count;
        }
    }

    private Cache<String, Window> windowsCache;

    private final RateLimitProperties properties;
    private final MeterRegistry meterRegistry;

    private List<String> pathPatterns;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public RateLimitingFilter(ObjectProvider<RateLimitProperties> properties, ObjectProvider<MeterRegistry> meterRegistry) {
        RateLimitProperties provided = properties.getIfAvailable();
        if (provided != null) {
            this.properties = provided;
        } else {
            RateLimitProperties fallback = new RateLimitProperties();
            fallback.setEnabled(false);
            fallback.setIncludeHeaders(false);
            this.properties = fallback;
        }
        this.meterRegistry = meterRegistry.getIfAvailable(SimpleMeterRegistry::new);
    }

    @PostConstruct
    void init() {
        this.windowsCache = Caffeine.newBuilder()
                .expireAfterWrite(properties.getWindowMs(), TimeUnit.MILLISECONDS)
                .maximumSize(properties.getCacheMaxIps())
                .build();

        List<String> configured = properties.getPaths();
        this.pathPatterns = configured != null ? configured.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList()) : List.of();
        logger.info("RateLimitingFilter initialized: enabled={}, windowMs={}, maxRequests={}, trustProxy={}, cacheMaxIps={}, includeHeaders={}, patterns={}",
                properties.isEnabled(), properties.getWindowMs(), properties.getMaxRequests(), properties.isTrustProxy(),
                properties.getCacheMaxIps(), properties.isIncludeHeaders(), pathPatterns);
    }

    private boolean isRateLimitedPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String pattern : pathPatterns) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private String extractIp(String val) {
        if (val == null) {
            return null;
        }
        String v = val.trim();
        if (v.startsWith("\"") && v.endsWith("\"") && v.length() > 1) {
            v = v.substring(1, v.length() - 1);
        }
        if (v.startsWith("[")) {
            int end = v.indexOf(']');
            if (end > 0) {
                return v.substring(1, end);
            }
            return v;
        }
        long colonCount = v.chars().filter(ch -> ch == ':').count();
        if (colonCount == 1 && v.contains(".")) {
            int colonIdx = v.indexOf(':');
            return v.substring(0, colonIdx);
        }
        return v;
    }

    private String clientIp(HttpServletRequest request) {
        if (!properties.isTrustProxy()) {
            return request.getRemoteAddr();
        }

        String remoteAddr = request.getRemoteAddr();
        if (properties.getAllowedProxies() != null && !properties.getAllowedProxies().isEmpty()) {
            if (!isAllowedProxy(remoteAddr)) {
                return remoteAddr;
            }
        }

        String fwd = request.getHeader("Forwarded");
        if (fwd != null && !fwd.isBlank()) {
            try {
                String[] parts = fwd.split(",");
                for (String part : parts) {
                    String[] kvs = part.split(";");
                    for (String kv : kvs) {
                        String[] pair = kv.trim().split("=", 2);
                        if (pair.length == 2 && pair[0].trim().equalsIgnoreCase("for")) {
                            return extractIp(pair[1]);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            int comma = xff.indexOf(',');
            String first = (comma > 0 ? xff.substring(0, comma) : xff).trim();
            return extractIp(first);
        }

        return request.getRemoteAddr();
    }

    private boolean isAllowedProxy(String remoteAddr) {
        try {
            for (String allowed : properties.getAllowedProxies()) {
                if (allowed != null && !allowed.isBlank() && remoteAddr.equals(allowed.trim())) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        boolean matched = isRateLimitedPath(request);
        if (!properties.isEnabled() || !matched) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            meterRegistry.counter("app.ratelimit.matched").increment();
        } catch (Exception ignored) {
        }

        final long now = System.currentTimeMillis();
        final String key = clientIp(request);

        Window w = windowsCache.asMap().compute(key, (k, old) -> {
            if (old == null || now - old.startMs >= properties.getWindowMs()) {
                return new Window(now, 1);
            } else {
                return new Window(old.startMs, old.count + 1);
            }
        });

        int remaining = Math.max(0, properties.getMaxRequests() - w.count);
        long resetMs = Math.max(0, properties.getWindowMs() - (now - w.startMs));

        if (properties.isIncludeHeaders()) {
            response.setHeader("X-RateLimit-Limit", String.valueOf(properties.getMaxRequests()));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, remaining)));
            response.setHeader("X-RateLimit-Reset", String.valueOf(Duration.ofMillis(resetMs).toSeconds()));
        }

        if (w.count > properties.getMaxRequests()) {
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf((int) Math.ceil(resetMs / 1000.0)));
            response.setContentType("application/json;charset=UTF-8");
            String body = "{\"error\":\"Too Many Requests\",\"retryAfterSeconds\":" + (int) Math.ceil(resetMs / 1000.0) + "}";
            response.getWriter().write(body);
            try {
                meterRegistry.counter("app.ratelimit.blocked").increment();
            } catch (Exception ignored) {
            }
            return;
        }

        filterChain.doFilter(request, response);
    }
}
