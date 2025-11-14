// Package declaration - defines that this class belongs to the config package
package com.zerox80.riotapi.config;

// Import for @Bean annotation to define Spring-managed objects
import org.springframework.context.annotation.Bean;
// Import for @Configuration annotation to mark this class as a configuration class
import org.springframework.context.annotation.Configuration;

// Import for Java 11+ HttpClient for modern HTTP communication
import java.net.http.HttpClient;
// Import for Duration to define time spans (timeouts)
import java.time.Duration;
// Import for ExecutorService for managing thread pools
import java.util.concurrent.ExecutorService;
// Import for Executors factory class for creating thread pools
import java.util.concurrent.Executors;
// Import for Executor interface for asynchronous task execution
import java.util.concurrent.Executor;


// @Configuration - marks this class as a Spring configuration class
// Contains @Bean methods that define Spring-managed objects
@Configuration
/**
 * HttpClientConfig configures HTTP clients and executors for Riot API communication.
 * Uses virtual threads (Java 21+) for massive parallelism with minimal memory footprint.
 * Configures HTTP/2 for better performance and wraps executors with MDC propagation for logging.
 */
public class HttpClientConfig {

    /**
     * Creates an ExecutorService using virtual threads for Riot API requests.
     * Virtual threads are lightweight and enable massive parallelism - ideal for I/O-intensive tasks.
     * PERFORMANCE: Thousands of concurrent HTTP requests possible with minimal memory overhead.
     *
     * @return ExecutorService with virtual thread pool
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService riotApiExecutorService() {
        // Create an ExecutorService with virtual threads (Java 21+ feature)
        // Virtual threads are lightweight and enable MASSIVE parallelism
        // Ideal for I/O-intensive tasks like HTTP requests (thousands parallel possible)
        // PERFORMANCE: Minimal memory footprint compared to platform threads
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * Creates an Executor that propagates MDC context across thread boundaries.
     * Wraps the base executor to preserve logging context (request IDs) in async operations.
     * IMPORTANT: Without this wrapper, request IDs would be lost in async threads.
     *
     * @param riotApiExecutorService The base executor service to wrap
     * @return Executor with MDC propagation support
     */
    @Bean
    public Executor riotApiMdcExecutor(ExecutorService riotApiExecutorService) {
        // Wrap the ExecutorService in an MDC-propagating executor
        // MDC = Mapped Diagnostic Context (SLF4J) - stores thread-local logging data
        // IMPORTANT: Without this wrapper, request IDs in async threads would be lost
        // This enables request tracing across asynchronous calls
        return new MdcPropagatingExecutor(riotApiExecutorService);
    }

    /**
     * Creates the HttpClient for Riot API communication.
     * Configured with HTTP/2 for better performance, 10-second connect timeout,
     * and MDC-propagating executor for correlated logging.
     *
     * @param riotApiMdcExecutor The executor with MDC propagation for async requests
     * @return Configured HttpClient for Riot API calls
     */
    @Bean
    public HttpClient riotApiHttpClient(Executor riotApiMdcExecutor) {
        // Create and configure an HttpClient with builder pattern
        return HttpClient.newBuilder()
                // Use HTTP/2 for better performance (multiplexing, header compression)
                // PERFORMANCE: Multiple requests over a single TCP connection possible
                .version(HttpClient.Version.HTTP_2)
                // Set connect timeout to 10 seconds
                // SECURITY: Prevents threads from waiting indefinitely for connection establishment
                .connectTimeout(Duration.ofSeconds(10))
                // Use the MDC-propagating executor for asynchronous requests
                // Ensures logging context (request IDs) is preserved in async calls
                .executor(riotApiMdcExecutor)
                // Finalize configuration and create the HttpClient
                .build();
    }
}
