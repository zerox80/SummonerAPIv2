// Package declaration - defines that this class belongs to the config package
package com.zerox80.riotapi.config;

// Import for MDC (Mapped Diagnostic Context) - thread-local context for logging
import org.slf4j.MDC;

// Import for Map to store key-value pairs (MDC context data)
import java.util.Map;
// Import for Executor interface for asynchronous task execution
import java.util.concurrent.Executor;


/**
 * MdcPropagatingExecutor wraps an Executor and propagates MDC context across thread boundaries.
 * This is crucial for maintaining request tracing information (like request IDs) in asynchronous operations.
 * Without this wrapper, logging context would be lost when tasks execute in different threads.
 * Implements the Decorator pattern to add MDC propagation to any Executor.
 */
public class MdcPropagatingExecutor implements Executor {

    // Final field for the underlying executor (Decorator pattern)
    // This performs the actual work, we just wrap the logic
    private final Executor delegate;

    /**
     * Constructor that accepts the executor to wrap.
     * Decorator pattern: extends functionality without modifying the original class.
     *
     * @param delegate The executor to wrap with MDC propagation
     */
    public MdcPropagatingExecutor(Executor delegate) {
        // Initialize the delegate field with the provided executor
        this.delegate = delegate;
    }

    /**
     * Executes the given command with MDC context propagation.
     * Copies the MDC context from the calling thread and restores it in the worker thread.
     * Ensures that logging statements in async tasks have access to request IDs and other context.
     *
     * @param command The task to execute
     */
    @Override
    public void execute(Runnable command) {
        // Create a COPY of the current MDC context map from the calling thread
        // Final = immutable - needed for lambda below
        // Contains e.g. requestId, userId, etc. for request tracing
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        // Pass a NEW Runnable to the delegated executor
        // Lambda expression that wraps the original command with MDC handling
        delegate.execute(() -> {
            // Save the possibly existing MDC context of the worker thread
            // Important if the thread is reused from a thread pool
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try {
                // Check if the calling thread had an MDC context
                if (contextMap != null) {
                    // Set the copied context as the current MDC context
                    // Now the worker thread has the same logging data as the caller
                    MDC.setContextMap(contextMap);
                } else {
                    // If no context existed, clear MDC completely
                    // Prevents old context from previous tasks from remaining
                    MDC.clear();
                }
                // Execute the actual command - NOW with correct MDC context
                // All log statements in command.run() have access to requestId etc.
                command.run();
            } finally {
                // Finally block ensures this code ALWAYS executes
                // Even if exceptions occur, the context is cleaned up
                if (previous != null) {
                    // Restore the original context of the worker thread
                    // IMPORTANT: Thread pools reuse threads - context must be cleaned up
                    MDC.setContextMap(previous);
                } else {
                    // If no previous context existed, clear MDC completely
                    // SECURITY: Prevents context leaks between different requests
                    MDC.clear();
                }
            }
        });
    }
}
