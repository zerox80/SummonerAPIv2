package com.zerox80.riotapi.config;

import com.zerox80.riotapi.service.DataDragonService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        // Ensure tests do not require external DB or migrations
        "spring.flyway.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:cspnonce;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none"
})
class CspNonceFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataDragonService dataDragonService;

    @Test
    void index_shouldIncludeCspHeader_andRenderNonceInScript() throws Exception {
        // Arrange DataDragonService to avoid any external calls during template rendering
        Map<String, String> bases = new HashMap<>();
        bases.put("champSquare", "/images/champ/");
        bases.put("rankedMiniCrest", "/images/ranked/");
        Mockito.when(dataDragonService.getImageBases(null)).thenReturn(bases);
        Mockito.when(dataDragonService.getChampionKeyToSquareUrl(any(Locale.class)))
                .thenReturn(Collections.emptyMap());

        // Act
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();

        String csp = result.getResponse().getHeader("Content-Security-Policy");
        String html = result.getResponse().getContentAsString();

        // Assert: Header is present and contains a nonce
        assertThat(csp).isNotBlank();
        assertThat(csp).contains("script-src");
        assertThat(csp).contains("'nonce-");

        // Extract the nonce value used in the header and verify it appears on the inline script tag
        Pattern p = Pattern.compile("script-src[^;]*'nonce-([^']+)'", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(csp);
        assertThat(m.find()).isTrue();
        String nonce = m.group(1);
        assertThat(nonce).isNotBlank();
        assertThat(html).contains("nonce=\"" + nonce + "\"");
    }
}
