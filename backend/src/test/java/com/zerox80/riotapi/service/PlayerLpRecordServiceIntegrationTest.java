package com.zerox80.riotapi.service;

import com.zerox80.riotapi.model.InfoDto;
import com.zerox80.riotapi.model.LeagueEntryDTO;
import com.zerox80.riotapi.model.MatchV5Dto;
import com.zerox80.riotapi.model.MetadataDto;
import com.zerox80.riotapi.model.PlayerLpRecord;
import com.zerox80.riotapi.model.Summoner;
import com.zerox80.riotapi.repository.PlayerLpRecordRepository;
import org.junit.jupiter.api.Test;
import com.zerox80.riotapi.client.RiotApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        // Ensure tests do not require external DB or migrations
        "spring.flyway.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:lprec;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PlayerLpRecordServiceIntegrationTest {

    @MockBean
    private RiotApiClient riotApiClient;

    @Autowired
    private PlayerLpRecordService playerLpRecordService;

    @Autowired
    private PlayerLpRecordRepository playerLpRecordRepository;

    @Test
    void testSavePlayerLpRecords_savesRecordsCorrectly() {
        // Arrange
        String puuid = "test-puuid";
        Instant timestamp = Instant.now();

        LeagueEntryDTO soloQueueEntry = new LeagueEntryDTO();
        soloQueueEntry.setQueueType("RANKED_SOLO_5x5");
        soloQueueEntry.setTier("GOLD");
        soloQueueEntry.setRank("IV");
        soloQueueEntry.setLeaguePoints(50);

        LeagueEntryDTO flexQueueEntry = new LeagueEntryDTO();
        flexQueueEntry.setQueueType("RANKED_FLEX_SR");
        flexQueueEntry.setTier("SILVER");
        flexQueueEntry.setRank("I");
        flexQueueEntry.setLeaguePoints(25);

        List<LeagueEntryDTO> entries = List.of(soloQueueEntry, flexQueueEntry);

        // Act
        playerLpRecordService.savePlayerLpRecords(puuid, entries, timestamp);

        // Assert
        List<PlayerLpRecord> savedRecords = playerLpRecordRepository.findAll();
        assertThat(savedRecords).hasSize(2);

        PlayerLpRecord soloRecord = savedRecords.stream()
                .filter(r -> r.getQueueType().equals("RANKED_SOLO_5x5"))
                .findFirst().orElseThrow();

        assertThat(soloRecord.getPuuid()).isEqualTo(puuid);
        assertThat(soloRecord.getTier()).isEqualTo("GOLD");
        assertThat(soloRecord.getRank()).isEqualTo("IV");
        assertThat(soloRecord.getLeaguePoints()).isEqualTo(50);
        assertThat(soloRecord.getTimestamp()).isEqualTo(timestamp);

        PlayerLpRecord flexRecord = savedRecords.stream()
                .filter(r -> r.getQueueType().equals("RANKED_FLEX_SR"))
                .findFirst().orElseThrow();

        assertThat(flexRecord.getPuuid()).isEqualTo(puuid);
        assertThat(flexRecord.getTier()).isEqualTo("SILVER");
        assertThat(flexRecord.getRank()).isEqualTo("I");
        assertThat(flexRecord.getLeaguePoints()).isEqualTo(25);
    }

    @Test
    void testSavePlayerLpRecords_ignoresNonRankedQueues() {
        // Arrange
        String puuid = "test-puuid-2";
        Instant timestamp = Instant.now();

        LeagueEntryDTO aramEntry = new LeagueEntryDTO();
        aramEntry.setQueueType("ARAM");
        aramEntry.setLeaguePoints(100);

        List<LeagueEntryDTO> entries = List.of(aramEntry);

        // Act
        playerLpRecordService.savePlayerLpRecords(puuid, entries, timestamp);

        // Assert
        List<PlayerLpRecord> savedRecords = playerLpRecordRepository.findByPuuid(puuid);
        assertThat(savedRecords).isEmpty();
    }

    @Test
    void testCalculateAndSetLpChanges_handlesNullTierAndRank() {
        String puuid = "null-tier-rank-puuid";
        Instant beforeTime = Instant.now();
        Instant afterTime = beforeTime.plusSeconds(120);
        Instant matchEnd = beforeTime.plusSeconds(60);

        PlayerLpRecord recordBefore = new PlayerLpRecord(puuid, "RANKED_SOLO_5x5", beforeTime, 100, null, null);
        PlayerLpRecord recordAfter = new PlayerLpRecord(puuid, "RANKED_SOLO_5x5", afterTime, 110, null, null);
        playerLpRecordRepository.save(recordBefore);
        playerLpRecordRepository.save(recordAfter);

        Summoner summoner = new Summoner();
        summoner.setPuuid(puuid);

        InfoDto info = new InfoDto();
        info.setQueueId(420);
        info.setGameEndTimestamp(matchEnd.toEpochMilli());
        MatchV5Dto match = new MatchV5Dto();
        match.setInfo(info);
        match.setMetadata(new MetadataDto());

        playerLpRecordService.calculateAndSetLpChangesForMatches(summoner, List.of(match));

        assertThat(match.getInfo().getLpChange()).isEqualTo(10);
    }
}
