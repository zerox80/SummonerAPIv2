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
        // Ensure tests do not require external DB or migrations
        "spring.flyway.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:reqid;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none"
})
class RequestIdFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void when_no_header_then_generated_request_id_is_returned_and_sane() throws Exception {
        var res = mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        String id = res.getHeader("X-Request-Id");
        assertThat(id).isNotNull();
        assertThat(id.length()).isBetween(1, 64);
        // Only allow safe characters [A-Za-z0-9_.-]
        assertThat(id).matches("[A-Za-z0-9_.-]+");
    }

    @Test
    void when_header_contains_crlf_and_symbols_then_it_is_sanitized_and_no_header_injection_occurs() throws Exception {
        String raw = "abc\r\nInjected: evil%$@! \n";
        // After sanitation: remove CR/LF and non [A-Za-z0-9_.-]
        String expected = "abcInjectedevil";

        var res = mockMvc.perform(get("/actuator/health").header("X-Request-Id", raw))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        String id = res.getHeader("X-Request-Id");
        assertThat(id).isEqualTo(expected);
        // Ensure no CR/LF present
        assertThat(id.contains("\r")).isFalse();
        assertThat(id.contains("\n")).isFalse();
        // Ensure only safe chars remain
        assertThat(id).matches("[A-Za-z0-9_.-]+");
    }

    @Test
    void when_header_too_long_then_truncated_to_64_chars() throws Exception {
        String raw = "x".repeat(100);
        var res = mockMvc.perform(get("/actuator/health").header("X-Request-Id", raw))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        String id = res.getHeader("X-Request-Id");
        assertThat(id).isNotNull();
        assertThat(id.length()).isEqualTo(64);
        assertThat(id).isEqualTo("x".repeat(64));
    }
}
