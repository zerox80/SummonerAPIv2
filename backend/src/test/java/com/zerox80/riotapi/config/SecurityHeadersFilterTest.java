package com.zerox80.riotapi.config;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityHeadersFilterTest {

    @Test
    void shouldApplyDefaultHeadersOnSecureRequest() throws ServletException, IOException {
        SecurityHeadersProperties props = new SecurityHeadersProperties();
        SecurityHeadersFilter filter = new SecurityHeadersFilter(providerOf(props));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(true);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(response.getHeader("X-XSS-Protection")).isEqualTo("0");
        assertThat(response.getHeader("X-Frame-Options")).isEqualTo("DENY");
        assertThat(response.getHeader("Referrer-Policy")).isEqualTo("strict-origin-when-cross-origin");
        assertThat(response.getHeader("Content-Security-Policy"))
                .contains("default-src")
                .contains("frame-ancestors 'none'");
        assertThat(response.getHeader("Strict-Transport-Security"))
                .contains("max-age=")
                .contains("includeSubDomains");
    }

    @Test
    void shouldOmitHstsForHttpWhenNotForced() throws ServletException, IOException {
        SecurityHeadersProperties props = new SecurityHeadersProperties();
        SecurityHeadersFilter filter = new SecurityHeadersFilter(providerOf(props));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(false);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader("Strict-Transport-Security")).isNull();
    }

    @Test
    void shouldForceHstsWhenConfigured() throws ServletException, IOException {
        SecurityHeadersProperties props = new SecurityHeadersProperties();
        props.setHstsForceHttp(true);
        props.setHstsPreload(true);
        props.setHstsMaxAgeSeconds(123);

        SecurityHeadersFilter filter = new SecurityHeadersFilter(providerOf(props));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSecure(false);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader("Strict-Transport-Security"))
                .isEqualTo("max-age=123; includeSubDomains; preload");
}

    private static ObjectProvider<SecurityHeadersProperties> providerOf(SecurityHeadersProperties value) {
        return new ObjectProvider<>() {
            @Override
            public SecurityHeadersProperties getObject(Object... args) {
                return value;
            }

            @Override
            public SecurityHeadersProperties getObject() {
                return value;
            }

            @Override
            public SecurityHeadersProperties getIfAvailable() {
                return value;
            }

            @Override
            public SecurityHeadersProperties getIfAvailable(Supplier<SecurityHeadersProperties> defaultSupplier) {
                return value != null ? value : defaultSupplier.get();
            }

            @Override
            public SecurityHeadersProperties getIfUnique() {
                return value;
            }

            @Override
            public SecurityHeadersProperties getIfUnique(Supplier<SecurityHeadersProperties> defaultSupplier) {
                return value != null ? value : defaultSupplier.get();
            }

            @Override
            public void forEach(Consumer<? super SecurityHeadersProperties> action) {
                if (value != null) {
                    action.accept(value);
                }
            }

            @Override
            public Stream<SecurityHeadersProperties> stream() {
                return value != null ? Stream.of(value) : Stream.empty();
            }

            @Override
            public Stream<SecurityHeadersProperties> orderedStream() {
                return stream();
            }
        };
    }
}
