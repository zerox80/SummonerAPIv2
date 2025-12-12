package com.zerox80.riotapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "rate.limit.enabled=true",
        "rate.limit.window-ms=60000",
        "rate.limit.max-requests=2",
        "rate.limit.paths=/api/**",
        "rate.limit.trust-proxy=true",
        // Ensure tests do not require external DB or migrations
        "spring.flyway.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:rlimitproxy;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none"
})
class RateLimitingFilterProxyTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRespectXForwardedFor_whenTrustProxyEnabled() throws Exception {
        var r1 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "x")
                        .header("X-Forwarded-For", "203.0.113.10"))
                .andExpect(status().isOk()).andReturn();
        assertThat(r1.getResponse().getHeader("X-RateLimit-Limit")).isEqualTo("2");

        var r2 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "y")
                        .header("X-Forwarded-For", "203.0.113.10"))
                .andExpect(status().isOk()).andReturn();
        assertThat(r2.getResponse().getHeader("X-RateLimit-Remaining")).isNotBlank();

        var r3 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "z")
                        .header("X-Forwarded-For", "203.0.113.10"))
                .andExpect(status().isTooManyRequests()).andReturn();
        assertThat(r3.getResponse().getHeader("Retry-After")).isNotBlank();
    }

    @Test
    void shouldParseForwardedHeader_whenTrustProxyEnabled() throws Exception {
        var r1 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "a")
                        .header("Forwarded", "for=198.51.100.23"))
                .andExpect(status().isOk()).andReturn();
        assertThat(r1.getResponse().getHeader("X-RateLimit-Limit")).isEqualTo("2");

        var r2 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "b")
                        .header("Forwarded", "for=198.51.100.23"))
                .andExpect(status().isOk()).andReturn();
        assertThat(r2.getResponse().getHeader("X-RateLimit-Remaining")).isNotBlank();

        var r3 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "c")
                        .header("Forwarded", "for=198.51.100.23"))
                .andExpect(status().isTooManyRequests()).andReturn();
        assertThat(r3.getResponse().getHeader("Retry-After")).isNotBlank();
    }

    @Test
    void shouldRespectXForwardedForIPv6_whenTrustProxyEnabled() throws Exception {
        var r1 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "i")
                        .header("X-Forwarded-For", "2001:db8:cafe::17"))
                .andExpect(status().isOk()).andReturn();
        assertThat(r1.getResponse().getHeader("X-RateLimit-Limit")).isEqualTo("2");

        var r2 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "j")
                        .header("X-Forwarded-For", "2001:db8:cafe::17"))
                .andExpect(status().isOk()).andReturn();
        assertThat(r2.getResponse().getHeader("X-RateLimit-Remaining")).isNotBlank();

        var r3 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "k")
                        .header("X-Forwarded-For", "2001:db8:cafe::17"))
                .andExpect(status().isTooManyRequests()).andReturn();
        assertThat(r3.getResponse().getHeader("Retry-After")).isNotBlank();
    }

    @Test
    void shouldParseForwardedHeaderIPv6_whenTrustProxyEnabled() throws Exception {
        var r1 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "m")
                        .header("Forwarded", "for=\"[2001:db8:cafe::18]:4711\""))
                .andExpect(status().isOk()).andReturn();
        assertThat(r1.getResponse().getHeader("X-RateLimit-Limit")).isEqualTo("2");

        var r2 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "n")
                        .header("Forwarded", "for=\"[2001:db8:cafe::18]:4711\""))
                .andExpect(status().isOk()).andReturn();
        assertThat(r2.getResponse().getHeader("X-RateLimit-Remaining")).isNotBlank();

        var r3 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "o")
                        .header("Forwarded", "for=\"[2001:db8:cafe::18]:4711\""))
                .andExpect(status().isTooManyRequests()).andReturn();
        assertThat(r3.getResponse().getHeader("Retry-After")).isNotBlank();
    }
}
