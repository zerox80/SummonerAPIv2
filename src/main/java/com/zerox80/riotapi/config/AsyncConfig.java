package com.zerox80.riotapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.slf4j.MDC;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Configuration class for asynchronous task execution in the application.
 * 
 * <p>This configuration sets up a custom thread pool executor for handling asynchronous
 * tasks with proper MDC (Mapped Diagnostic Context) propagation. This ensures that
 * contextual information like request IDs is preserved across asynchronous operations.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Custom thread pool with configurable core and maximum pool sizes</li>
 *   <li>MDC context propagation for request tracking across async boundaries</li>
 *   <li>Caller-runs policy to prevent task loss under high load</li>
 *   <li>Descriptive thread naming for easier debugging and monitoring</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Configuration
public class AsyncConfig {

    /**
     * Creates and configures the primary application task executor for asynchronous operations.
     * 
     * <p>This executor is configured with a thread pool size suitable for the application's needs,
     * proper MDC propagation to maintain request context across async boundaries, and a caller-runs
     * policy to ensure tasks are not silently dropped under load conditions.</p>
     * 
     * @return A configured Executor instance for async task execution
     */
    @Bean(name = "appTaskExecutor")
    public Executor appTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("app-async-");
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(500);
        // Do not silently drop tasks under load
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // Propagate MDC (e.g., requestId) to async tasks
        executor.setTaskDecorator(runnable -> {
            final java.util.Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                java.util.Map<String, String> previous = MDC.getCopyOfContextMap();
                try {
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    } else {
                        MDC.clear();
                    }
                    runnable.run();
                } finally {
                    if (previous != null) {
                        MDC.setContextMap(previous);
                    } else {
                        MDC.clear();
                    }
                }
            };
        });
        executor.initialize();
        return executor;
    }
}
