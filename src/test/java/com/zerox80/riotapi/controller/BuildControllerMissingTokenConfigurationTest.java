package com.zerox80.riotapi.controller;

import com.zerox80.riotapi.client.RiotApiClient;
import com.zerox80.riotapi.service.BuildAggregationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BuildController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "build.agg.trigger-enabled=true",
        "build.agg.trigger-token="
})
class BuildControllerMissingTokenConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuildAggregationService aggregationService;

    @MockBean
    private RiotApiClient riotApiClient;

    @Test
    void aggregateShouldRejectWhenTokenNotConfigured() throws Exception {
        mockMvc.perform(post("/api/champions/Ahri/aggregate"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("not configured")));

        verifyNoInteractions(aggregationService);
    }
}
