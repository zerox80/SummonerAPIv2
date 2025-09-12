package com.zerox80.riotapi.config;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Wraps an Executor to propagate the SLF4J MDC context (e.g., requestId) from the submitting
 * thread to the worker thread. This ensures correlation IDs appear in logs emitted from
 * async/virtual threads used by the Java HttpClient and CompletableFutures.
 */
public class MdcPropagatingExecutor implements Executor {

    private final Executor delegate;

    public MdcPropagatingExecutor(Executor delegate) {
        this.delegate = delegate;
    }

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
