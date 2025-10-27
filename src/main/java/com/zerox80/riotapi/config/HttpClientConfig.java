package com.zerox80.riotapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;

/**
 * Configuration class for HTTP client used to communicate with the Riot Games API.
 * 
 * <p>This configuration sets up a high-performance HTTP client with virtual threads
 * for optimal concurrency and resource usage. It includes proper MDC propagation
 * to maintain request context across async operations and is optimized for HTTP/2.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Virtual threads for high concurrency with low memory footprint</li>
 *   <li>MDC propagation for request tracking across async boundaries</li>
 *   <li>HTTP/2 support for improved performance</li>
 *   <li>Configurable connection timeouts</li>
 *   <li>Proper executor lifecycle management</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Configuration
public class HttpClientConfig {

    /**
     * Creates an ExecutorService using virtual threads for high-concurrency operations.
     * 
     * <p>Virtual threads provide significantly better resource usage compared to platform threads,
     * allowing for much higher concurrency with lower memory overhead. This is ideal for I/O-bound
     * operations like HTTP requests to external APIs.</p>
     * 
     * @return An ExecutorService configured with virtual thread per task execution
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService riotApiExecutorService() {
        // Virtual threads for high concurrency with low footprint
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * Wraps the virtual thread executor with MDC propagation capabilities.
     * 
     * <p>This method creates an MdcPropagatingExecutor that ensures the MDC context
     * (such as request IDs) is properly propagated from the submitting thread to the
     * virtual threads that execute HTTP requests. This maintains request correlation
     * across async boundaries for better debugging and monitoring.</p>
     * 
     * @param riotApiExecutorService The base ExecutorService to wrap with MDC propagation
     * @return An Executor that propagates MDC context to worker threads
     */
    @Bean
    public Executor riotApiMdcExecutor(ExecutorService riotApiExecutorService) {
        return new MdcPropagatingExecutor(riotApiExecutorService);
    }

    /**
     * Creates and configures the HTTP client for Riot API communication.
     * 
     * <p>This method builds an HttpClient optimized for external API communication with
     * HTTP/2 support, reasonable connection timeouts, and the MDC-propagating executor
     * for proper request tracking across async operations.</p>
     * 
     * @param riotApiMdcExecutor The executor with MDC propagation for async operations
     * @return A configured HttpClient instance ready for Riot API requests
     */
    @Bean
    public HttpClient riotApiHttpClient(Executor riotApiMdcExecutor) {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .executor(riotApiMdcExecutor)
                .build();
    }
}
