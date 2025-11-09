package com.zerox80.riotapi.config;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Executor;


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
