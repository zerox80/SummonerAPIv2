package com.zerox80.riotapi.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.cache.support.NoOpCacheManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiotApiClientConcurrencyTest {

    @Test
    void acquirePermitAsyncHandlesHighLoad() throws Exception {
        RiotApiClient client = new RiotApiClient(
                "test-key",
                "na1",
                "https://example.com",
                "JUnit/ConcurrencyTest",
                new ObjectMapper(),
                new SimpleMeterRegistry(),
                HttpClient.newHttpClient(),
                1,
                new NoOpCacheManager()
        );

        Field limiterField = RiotApiClient.class.getDeclaredField("outboundLimiter");
        limiterField.setAccessible(true);
        Semaphore limiter = (Semaphore) limiterField.get(client);

        limiter.acquire();

        Method acquireMethod = RiotApiClient.class.getDeclaredMethod("acquirePermitAsync");
        acquireMethod.setAccessible(true);

        int requestCount = 50;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < requestCount; i++) {
            CompletableFuture<Void> cf = (CompletableFuture<Void>) acquireMethod.invoke(client);
            futures.add(cf.thenRun(limiter::release));
        }

        CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(limiter::release);

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(5, TimeUnit.SECONDS);

        assertEquals(1, limiter.availablePermits());
    }
}
