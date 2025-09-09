package com.zerox80.riotapi.service;

import com.zerox80.riotapi.client.RiotApiClient;
import com.zerox80.riotapi.model.InfoDto;
import com.zerox80.riotapi.model.MatchV5Dto;
import com.zerox80.riotapi.model.ParticipantDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RiotApiServiceTest {

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

        RiotApiService service = new RiotApiService((RiotApiClient) null, (PlayerLpRecordService) null);

        Map<String, Long> counts = service.getChampionPlayCounts(List.of(m1, m2), puuid);

        assertThat(counts.get("Ahri")).isEqualTo(2L);
        assertThat(counts.get("Lux")).isEqualTo(1L);
        assertThat(counts).doesNotContainKey("Zed");
    }
}
