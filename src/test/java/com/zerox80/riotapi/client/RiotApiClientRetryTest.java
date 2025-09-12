package com.zerox80.riotapi.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.Authenticator;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.PushPromiseHandler;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "riot.api.key=TEST",
        "riot.api.region=euw1",
        // Avoid hitting DB in this test
        "spring.flyway.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:riotclient;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none"
})
class RiotApiClientRetryTest {

    @Autowired
    private RiotApiClient client;

    @Test
    void shouldRetryOn429AndSucceed() {
        // First response will be 429 with Retry-After, second will be 200 with JSON body
        var result = client.getMatchIdsByPuuid("PUUID-TEST", 2).join();
        assertThat(result).isNotNull();
        assertThat(result).containsExactly("match1", "match2");
    }

    @TestConfiguration
    static class StubHttpClientConfig {
        @Bean
        public HttpClient riotApiHttpClient() {
            return new StubHttpClient();
        }
    }

    static class StubHttpClient extends HttpClient {
        private final AtomicInteger calls = new AtomicInteger(0);

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            int c = calls.incrementAndGet();
            if (c == 1) {
                // Return 429 with Retry-After header
                return CompletableFuture.completedFuture(new SimpleHttpResponse<>(
                        request,
                        429,
                        HttpHeaders.of(Map.of("Retry-After", List.of("1")), (k, v) -> true),
                        castBody(responseBodyHandler, "{\"error\":\"Too Many Requests\"}"),
                        request.uri(),
                        HttpClient.Version.HTTP_2
                ));
            }
            // Success with simple match id list JSON
            return CompletableFuture.completedFuture(new SimpleHttpResponse<>(
                    request,
                    200,
                    HttpHeaders.of(Map.of(), (k, v) -> true),
                    castBody(responseBodyHandler, "[\"match1\",\"match2\"]"),
                    request.uri(),
                    HttpClient.Version.HTTP_2
            ));
        }

        @SuppressWarnings("unchecked")
        private <T> T castBody(HttpResponse.BodyHandler<T> handler, String value) {
            // We return the raw String body as many handlers will just accept a String for tests
            return (T) value;
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
            return sendAsync(request, responseBodyHandler).join();
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler, PushPromiseHandler<T> pushPromiseHandler) {
            return sendAsync(request, responseBodyHandler);
        }

        // Optional overrides to satisfy abstract methods; unused in tests
        @Override public Optional<CookieHandler> cookieHandler() { return Optional.empty(); }
        @Override public Optional<Duration> connectTimeout() { return Optional.of(Duration.ofSeconds(2)); }
        @Override public Redirect followRedirects() { return Redirect.NEVER; }
        @Override public Optional<ProxySelector> proxy() { return Optional.empty(); }
        @Override public SSLContext sslContext() { return null; }
        @Override public SSLParameters sslParameters() { return null; }
        @Override public Optional<Executor> executor() { return Optional.empty(); }
        @Override public Version version() { return Version.HTTP_2; }
        @Override public Optional<Authenticator> authenticator() { return Optional.empty(); }
    }

    static class SimpleHttpResponse<T> implements HttpResponse<T> {
        private final HttpRequest request;
        private final int status;
        private final HttpHeaders headers;
        private final T body;
        private final URI uri;
        private final HttpClient.Version version;

        SimpleHttpResponse(HttpRequest request, int status, HttpHeaders headers, T body, URI uri, HttpClient.Version version) {
            this.request = request;
            this.status = status;
            this.headers = headers;
            this.body = body;
            this.uri = uri;
            this.version = version;
        }

        @Override public int statusCode() { return status; }
        @Override public HttpRequest request() { return request; }
        @Override public Optional<HttpResponse<T>> previousResponse() { return Optional.empty(); }
        @Override public HttpHeaders headers() { return headers; }
        @Override public T body() { return body; }
        @Override public Optional<SSLSession> sslSession() { return Optional.empty(); }
        @Override public URI uri() { return uri; }
        @Override public HttpClient.Version version() { return version; }
    }
}
