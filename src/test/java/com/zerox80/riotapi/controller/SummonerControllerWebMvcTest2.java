package com.zerox80.riotapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerox80.riotapi.model.Summoner;
import com.zerox80.riotapi.model.SummonerSuggestionDTO;
import com.zerox80.riotapi.service.DataDragonService;
import com.zerox80.riotapi.service.RiotApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SummonerController.class)
@AutoConfigureMockMvc(addFilters = false)
class SummonerControllerWebMvcTest2 {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RiotApiService riotApiService;

    @MockBean
    private DataDragonService dataDragonService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void index_shouldRenderOk() throws Exception {
        when(dataDragonService.getImageBases(null)).thenReturn(Map.of(
                "champSquare", "https://cdn/img/champion/",
                "rankedMiniCrest", "https://assets/",
                "version", "latest"
        ));
        when(dataDragonService.getChampionKeyToSquareUrl(any(Locale.class)))
                .thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void suggestions_shouldReturnJson() throws Exception {
        when(riotApiService.getSummonerSuggestions(eq("ah"), anyMap()))
                .thenReturn(java.util.List.of(new SummonerSuggestionDTO("Player#EUW", 123, 30, "https://icon/url.jpg")));

        mockMvc.perform(get("/api/summoner-suggestions").param("query", "ah"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Player#EUW")));
    }

    @Test
    void me_withoutBearer_shouldBe401() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Missing or invalid Authorization header")));
    }

    @Test
    void matches_shouldRejectWhenCountExceedsLimit() throws Exception {
        mockMvc.perform(get("/api/matches")
                        .param("riotId", "Player#EUW")
                        .param("count", "41"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Maximum matches per request")));
    }

    @Test
    void matches_shouldSanitizeStartAndPassToService() throws Exception {
        Summoner summoner = new Summoner();
        summoner.setPuuid("PUUID-1");
        when(riotApiService.getSummonerByRiotId("Player", "EUW"))
                .thenReturn(CompletableFuture.completedFuture(summoner));
        when(riotApiService.getMatchHistoryPaged("PUUID-1", 0, 5))
                .thenReturn(CompletableFuture.completedFuture(List.of()));

        mockMvc.perform(get("/api/matches")
                        .param("riotId", "Player#EUW")
                        .param("start", "-10")
                        .param("count", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

        verify(riotApiService).getMatchHistoryPaged("PUUID-1", 0, 5);
    }
}
