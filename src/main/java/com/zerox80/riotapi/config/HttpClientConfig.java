package com.zerox80.riotapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;


@Configuration
public class HttpClientConfig {

    
    @Bean(destroyMethod = "shutdown")
    public ExecutorService riotApiExecutorService() {
        // Virtual threads for high concurrency with low footprint
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    
    @Bean
    public Executor riotApiMdcExecutor(ExecutorService riotApiExecutorService) {
        return new MdcPropagatingExecutor(riotApiExecutorService);
    }

    
    @Bean
    public HttpClient riotApiHttpClient(Executor riotApiMdcExecutor) {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .executor(riotApiMdcExecutor)
                .build();
    }
}
