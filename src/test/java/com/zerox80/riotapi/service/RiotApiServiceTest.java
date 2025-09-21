package com.zerox80.riotapi.service;

import com.zerox80.riotapi.client.RiotApiClient;
import com.zerox80.riotapi.model.AccountDto;
import com.zerox80.riotapi.model.InfoDto;
import com.zerox80.riotapi.model.MatchV5Dto;
import com.zerox80.riotapi.model.ParticipantDto;
import com.zerox80.riotapi.model.Summoner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiotApiServiceTest {

    @Mock
    private RiotApiClient riotApiClient;

    @Mock
    private PlayerLpRecordService playerLpRecordService;

    @Test
    void getChampionPlayCounts_countsMatchesForGivenPuuid() {
        String puuid = "player-puuid";

        ParticipantDto p1 = new ParticipantDto();
        p1.setPuuid(puuid);
        p1.setChampionName("Ahri");

        ParticipantDto p2 = new ParticipantDto();
        p2.setPuuid("other");
        p2.setChampionName("Zed");

        InfoDto info1 = new InfoDto();
        info1.setParticipants(List.of(p1, p2));

        MatchV5Dto m1 = new MatchV5Dto();
        m1.setInfo(info1);

        ParticipantDto p3 = new ParticipantDto();
        p3.setPuuid(puuid);
        p3.setChampionName("Ahri");

        ParticipantDto p4 = new ParticipantDto();
        p4.setPuuid(puuid);
        p4.setChampionName("Lux");

        InfoDto info2 = new InfoDto();
        info2.setParticipants(List.of(p3, p4));

        MatchV5Dto m2 = new MatchV5Dto();
        m2.setInfo(info2);

        RiotApiService service = new RiotApiService(riotApiClient, playerLpRecordService);

        Map<String, Long> counts = service.getChampionPlayCounts(List.of(m1, m2), puuid);

        assertThat(counts.get("Ahri")).isEqualTo(2L);
        assertThat(counts.get("Lux")).isEqualTo(1L);
        assertThat(counts).doesNotContainKey("Zed");
    }

    @Test
    void getSummonerByRiotId_trimsInputsBeforeCallingClient() {
        RiotApiService service = new RiotApiService(riotApiClient, playerLpRecordService);

        AccountDto account = new AccountDto();
        account.setPuuid("P1");
        account.setGameName("Player");

        when(riotApiClient.getAccountByRiotId("Player", "TAG"))
                .thenReturn(CompletableFuture.completedFuture(account));

        Summoner summoner = new Summoner();
        summoner.setPuuid("P1");
        when(riotApiClient.getSummonerByPuuid("P1"))
                .thenReturn(CompletableFuture.completedFuture(summoner));

        service.getSummonerByRiotId(" Player ", " TAG ").join();

        verify(riotApiClient).getAccountByRiotId("Player", "TAG");
        verify(riotApiClient).getSummonerByPuuid("P1");
    }
}
