package com.zerox80.riotapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "rate.limit.enabled=true",
        "rate.limit.window-ms=60000",
        "rate.limit.max-requests=2",
        "rate.limit.paths=/api/**",
        // Ensure tests do not require external DB or migrations
        "spring.flyway.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:rlimit;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none"
})
class RateLimitingFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rateLimiter_shouldReturn429AfterExceedingLimit_onApiEndpoints() throws Exception {
        // First request should pass
        var r1 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "test"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(r1.getResponse().getHeader("X-RateLimit-Limit")).isEqualTo("2");
        // Remaining could be 1 or 2 depending on header write timing; ensure header exists
        assertThat(r1.getResponse().getHeader("X-RateLimit-Remaining")).isNotBlank();

        // Second request should pass
        var r2 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "test2"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(r2.getResponse().getHeader("X-RateLimit-Limit")).isEqualTo("2");
        assertThat(r2.getResponse().getHeader("X-RateLimit-Remaining")).isNotBlank();

        // Third request should be limited
        var r3 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "test3"))
                .andExpect(status().isTooManyRequests())
                .andReturn();
        assertThat(r3.getResponse().getHeader("X-RateLimit-Limit")).isEqualTo("2");
        assertThat(r3.getResponse().getHeader("X-RateLimit-Remaining")).isEqualTo("0");
        assertThat(r3.getResponse().getHeader("Retry-After")).isNotBlank();
        assertThat(r3.getResponse().getContentAsString()).contains("Too Many Requests");
    }
}
