package com.zerox80.riotapi.controller;

import com.zerox80.riotapi.client.RiotApiClient;
import com.zerox80.riotapi.model.ChampionSummary;
import com.zerox80.riotapi.service.BuildAggregationService;
import com.zerox80.riotapi.service.DataDragonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ChampionsController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChampionsControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataDragonService dataDragonService;

    @MockBean
    private BuildAggregationService buildAggregationService;

    @MockBean
    private RiotApiClient riotApiClient;

    @Test
    void championsPage_shouldRenderWithModel_whenServiceReturnsData() throws Exception {
        when(dataDragonService.getChampionSummaries(any(Locale.class)))
                .thenReturn(List.of(new ChampionSummary("Ahri", "Ahri", "the Nine-Tailed Fox", List.of("Mage"), "Ahri.png")));
        when(dataDragonService.getImageBases(null))
                .thenReturn(Map.of("version", "latest", "champSquare", "https://cdn/img/champion/", "rankedMiniCrest", "https://assets/"));

        mockMvc.perform(get("/champions"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("champions"))
                .andExpect(model().attributeExists("bases"))
                .andExpect(model().attributeExists("version"));
    }
}
