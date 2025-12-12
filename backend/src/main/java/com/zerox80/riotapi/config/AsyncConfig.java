// Package declaration - defines that this configuration class belongs to the config package
package com.zerox80.riotapi.config;

// Import for @Bean annotation - used to create Spring-managed beans
import org.springframework.context.annotation.Bean;
// Import for @Configuration annotation - marks this class as a configuration source
import org.springframework.context.annotation.Configuration;
// Import for Spring's ThreadPoolTaskExecutor - implementation for asynchronous task execution
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
// Import for Mapped Diagnostic Context - stores contextual logging information (like request IDs)
import org.slf4j.MDC;

// Import for Executor interface from Java Concurrency Framework
import java.util.concurrent.Executor;
// Import for rejection policies when thread pool is full
import java.util.concurrent.ThreadPoolExecutor;


// @Configuration - marks this class as a source of bean definitions for the Spring container
@Configuration
/**
 * AsyncConfig configures asynchronous task execution in the application.
 * Sets up a thread pool with MDC context propagation for correlated logging across threads.
 */
public class AsyncConfig {

    /**
     * Creates and configures the main task executor bean.
     * Configures thread pool size, queue capacity, rejection policy, and MDC propagation.
     *
     * @return Configured Executor for asynchronous task execution
     */
    @Bean(name = "appTaskExecutor")
    public Executor appTaskExecutor() {
        // Create new instance of ThreadPoolTaskExecutor (Spring's wrapper around ThreadPoolExecutor)
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Set prefix for thread names, makes debugging easier in thread dumps
        // Threads will be named: app-async-1, app-async-2, etc.
        executor.setThreadNamePrefix("app-async-");
        // Set core number of threads that always run (2 threads)
        // These threads are never terminated even when idle
        executor.setCorePoolSize(2);
        // Set maximum number of threads (8 threads)
        // Pool can grow up to 8 threads under high load
        executor.setMaxPoolSize(8);
        // Set queue capacity (500 tasks)
        // Tasks are queued when all core threads are busy
        executor.setQueueCapacity(500);
        // Do not silently drop tasks under load
        // Set rejection policy: CallerRunsPolicy runs task in calling thread
        // when pool and queue are full (backpressure mechanism)
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // Propagate MDC (e.g., requestId) to async tasks
        // Important for logging correlation across thread boundaries
        executor.setTaskDecorator(runnable -> {
            // Copy current MDC context map (with request ID etc.) from calling thread
            // getCopyOfContextMap() returns null if context is empty
            final java.util.Map<String, String> contextMap = MDC.getCopyOfContextMap();
            // Return new Runnable that restores the MDC context
            return () -> {
                // Save previous MDC context from worker thread
                // In case worker thread already had a context
                java.util.Map<String, String> previous = MDC.getCopyOfContextMap();
                try {
                    // Check if calling thread had a context
                    if (contextMap != null) {
                        // Set MDC context from calling thread in worker thread
                        // Restores request ID etc.
                        MDC.setContextMap(contextMap);
                    } else {
                        // Calling thread had no context - clear MDC in worker thread
                        MDC.clear();
                    }
                    // Execute the actual task (with correct MDC context)
                    runnable.run();
                } finally {
                    // ALWAYS executed, even on exceptions
                    if (previous != null) {
                        // Restore original worker thread context
                        // Important if thread is reused from pool
                        MDC.setContextMap(previous);
                    } else {
                        // Worker thread had no previous context - clear MDC for thread reuse
                        MDC.clear();
                    }
                }
            };
        });
        // Initialize the thread pool (starts core threads)
        // After this call, executor is ready for use
        executor.initialize();
        // Return configured executor for registration as Spring bean
        return executor;
    }
}
