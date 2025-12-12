package com.zerox80.riotapi.service;

import com.zerox80.riotapi.client.RiotApiClient;
import com.zerox80.riotapi.model.InfoDto;
import com.zerox80.riotapi.model.LeagueEntryDTO;
import com.zerox80.riotapi.model.MatchV5Dto;
import com.zerox80.riotapi.model.MetadataDto;
import com.zerox80.riotapi.model.ParticipantDto;
import com.zerox80.riotapi.model.Summoner;
import com.zerox80.riotapi.repository.ChampionItemStatRepository;
import com.zerox80.riotapi.repository.ChampionRuneStatRepository;
import com.zerox80.riotapi.repository.ChampionSpellPairStatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuildAggregationServiceTest {

    @Mock
    private RiotApiClient riotApiClient;
    @Mock
    private DataDragonService dataDragonService;
    @Mock
    private ChampionItemStatRepository itemRepo;
    @Mock
    private ChampionRuneStatRepository runeRepo;
    @Mock
    private ChampionSpellPairStatRepository spellRepo;

    private BuildAggregationService service;

    @BeforeEach
    void setup() {
        service = new BuildAggregationService(riotApiClient, dataDragonService, itemRepo, runeRepo, spellRepo, new NoOpTransactionManager());
        lenient().when(itemRepo.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(runeRepo.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(spellRepo.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void aggregateChampion_usesUniqueSummonerIdsUpToLimit() throws Exception {
        Locale locale = Locale.US;

        when(dataDragonService.getLatestShortPatch()).thenReturn("14.1");
        when(dataDragonService.getChampionKey("Ahri", locale)).thenReturn(103);

        LeagueEntryDTO entry1 = new LeagueEntryDTO();
        entry1.setSummonerId("S1");
        LeagueEntryDTO entry2 = new LeagueEntryDTO();
        entry2.setSummonerId("S1");
        LeagueEntryDTO entry3 = new LeagueEntryDTO();
        entry3.setSummonerId("S2");

        when(riotApiClient.getEntriesByQueueTierDivision(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(CompletableFuture.completedFuture(List.of(entry1, entry2, entry3)));

        Summoner summoner1 = new Summoner();
        summoner1.setPuuid("P1");
        Summoner summoner2 = new Summoner();
        summoner2.setPuuid("P2");

        when(riotApiClient.getSummonerById("S1")).thenReturn(CompletableFuture.completedFuture(summoner1));
        when(riotApiClient.getSummonerById("S2")).thenReturn(CompletableFuture.completedFuture(summoner2));

        when(riotApiClient.getMatchIdsByPuuid(anyString(), anyInt()))
                .thenAnswer(inv -> CompletableFuture.completedFuture(List.of("match-" + inv.getArgument(0))));

        MatchV5Dto match = buildMatchDto();
        when(riotApiClient.getMatchDetails(anyString()))
                .thenReturn(CompletableFuture.completedFuture(match));

        service.aggregateChampion("Ahri", 420, 1, 1, 2, locale);

        verify(riotApiClient, times(1)).getSummonerById("S1");
        verify(riotApiClient, times(1)).getSummonerById("S2");
        verify(riotApiClient, times(2)).getMatchIdsByPuuid(anyString(), anyInt());
        verify(itemRepo).deleteByChampionIdAndPatchAndQueueId("Ahri", "14.1", 420);
        verify(itemRepo).saveAll(any());
        verify(spellRepo).saveAll(any());
    }

    private MatchV5Dto buildMatchDto() {
        MatchV5Dto match = new MatchV5Dto();
        MetadataDto metadata = new MetadataDto();
        metadata.setMatchId("match");
        match.setMetadata(metadata);

        InfoDto info = new InfoDto();
        info.setQueueId(420);
        info.setGameVersion("14.1.1");
        info.setGameDuration(1200L);

        ParticipantDto participant = new ParticipantDto();
        participant.setChampionId(103);
        participant.setWin(true);
        participant.setItem0(1001);
        participant.setSummoner1Id(4);
        participant.setSummoner2Id(14);
        participant.setTeamPosition("MIDDLE");

        List<ParticipantDto> participants = new ArrayList<>();
        participants.add(participant);
        info.setParticipants(participants);

        match.setInfo(info);
        return match;
    }

    private static class NoOpTransactionManager extends AbstractPlatformTransactionManager {

    @Override
    protected Object doGetTransaction() {
        return new Object();
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        // no-op
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        // no-op
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        // no-op
    }
}

}
