package com.zerox80.riotapi.config;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Executor wrapper that propagates SLF4J MDC context across thread boundaries.
 * 
 * <p>This class ensures that contextual information like request IDs is properly
 * propagated from the submitting thread to worker threads. This is critical for
 * maintaining request correlation in logs when using async operations, virtual threads,
 * or the Java HttpClient with CompletableFutures.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Automatic MDC context capture and restoration</li>
 *   <li>Safe handling of null MDC contexts</li>
 *   <li>Transparent wrapper - works with any Executor implementation</li>
 *   <li>Essential for debugging async operations across thread boundaries</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class MdcPropagatingExecutor implements Executor {

    /** The underlying executor that actually executes the tasks */
    private final Executor delegate;

    /**
     * Constructs a new MdcPropagatingExecutor that wraps the specified delegate executor.
     * 
     * @param delegate The executor to wrap with MDC propagation capabilities
     */
    public MdcPropagatingExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    /**
     * Executes the given command with MDC context propagation.
     * 
     * <p>This method captures the current MDC context from the submitting thread,
     * then wraps the command to restore this context on the worker thread before
     * execution. This ensures that logging and other MDC-dependent operations
     * maintain the same contextual information across thread boundaries.</p>
     * 
     * @param command The Runnable to execute with propagated MDC context
     */
    @Override
    public void execute(Runnable command) {
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        delegate.execute(() -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                } else {
                    MDC.clear();
                }
                command.run();
            } finally {
                if (previous != null) {
                    MDC.setContextMap(previous);
                } else {
                    MDC.clear();
                }
            }
        });
    }
}
