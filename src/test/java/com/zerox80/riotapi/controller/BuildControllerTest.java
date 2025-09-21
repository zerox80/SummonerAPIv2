package com.zerox80.riotapi.controller;

import com.zerox80.riotapi.client.RiotApiClient;
import com.zerox80.riotapi.dto.ChampionBuildDto;
import com.zerox80.riotapi.service.BuildAggregationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BuildController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "build.agg.trigger-enabled=true",
        "build.agg.trigger-token=test-secret"
})
class BuildControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuildAggregationService aggregationService;

    @MockBean
    private RiotApiClient riotApiClient;

    @Test
    void getBuild_shouldReturnChampionBuild() throws Exception {
        ChampionBuildDto dto = new ChampionBuildDto("Ahri", "15.18", 420, "ALL", java.util.List.of(), java.util.List.of(), java.util.List.of());
        when(aggregationService.loadBuild(ArgumentMatchers.eq("Ahri"), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(Locale.class)))
                .thenReturn(dto);

        mockMvc.perform(get("/api/champions/Ahri/build"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ahri")));
    }

    @Test
    void aggregateWithoutToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/champions/Ahri/aggregate"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("token")));

        verifyNoInteractions(aggregationService);
    }

    @Test
    void aggregateWithToken_shouldStartAggregation() throws Exception {
        mockMvc.perform(post("/api/champions/Ahri/aggregate")
                        .header("X-Aggregation-Token", "test-secret"))
                .andExpect(status().isAccepted())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Aggregation started")));

        verify(aggregationService).aggregateChampion(ArgumentMatchers.eq("Ahri"), ArgumentMatchers.isNull(), ArgumentMatchers.eq(1), ArgumentMatchers.eq(8), ArgumentMatchers.eq(75), ArgumentMatchers.any(Locale.class));
    }
}
