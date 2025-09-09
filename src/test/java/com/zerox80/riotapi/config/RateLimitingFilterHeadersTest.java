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
        "rate.limit.include-headers=false",
        // Ensure tests do not require external DB or migrations
        "spring.flyway.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:rlimitheaders;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none"
})
class RateLimitingFilterHeadersTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldNotIncludeHeaders_whenIncludeHeadersDisabled() throws Exception {
        var r1 = mockMvc.perform(get("/api/summoner-suggestions").param("query", "test"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(r1.getResponse().getHeader("X-RateLimit-Limit")).isNull();
        assertThat(r1.getResponse().getHeader("X-RateLimit-Remaining")).isNull();
        assertThat(r1.getResponse().getHeader("X-RateLimit-Reset")).isNull();
    }
}
