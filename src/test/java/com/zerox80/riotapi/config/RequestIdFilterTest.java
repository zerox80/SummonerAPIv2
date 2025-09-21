package com.zerox80.riotapi.config;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class RequestIdFilterTest {

    private final RequestIdFilter filter = new RequestIdFilter();

    @Test
    void whenNoHeader_thenGeneratedRequestIdIsReturnedAndSane() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        String id = response.getHeader(RequestIdFilter.HEADER);
        assertThat(id).isNotBlank();
        assertThat(id.length()).isBetween(1, 64);
        assertThat(id).matches("[A-Za-z0-9_.-]+");
    }

    @Test
    void whenHeaderContainsCrLf_thenItIsSanitized() throws ServletException, IOException {
        String raw = "abc\r\nInjected: evil%$@! \n";
        String expected = "abcInjectedevil";

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
        request.addHeader(RequestIdFilter.HEADER, raw);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        String id = response.getHeader(RequestIdFilter.HEADER);
        assertThat(id).isEqualTo(expected);
        assertThat(id).matches("[A-Za-z0-9_.-]+");
    }

    @Test
    void whenHeaderTooLong_thenTruncatedTo64Chars() throws ServletException, IOException {
        String raw = "x".repeat(100);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
        request.addHeader(RequestIdFilter.HEADER, raw);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        String id = response.getHeader(RequestIdFilter.HEADER);
        assertThat(id).isEqualTo("x".repeat(64));
    }
}
