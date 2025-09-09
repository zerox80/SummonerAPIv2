package com.zerox80.riotapi.model;

import java.util.List;
import java.util.Map;

public record SummonerProfileData(
    Summoner summoner,
    List<LeagueEntryDTO> leagueEntries,
    List<MatchV5Dto> matchHistory,
    SummonerSuggestionDTO suggestion,
    Map<String, Long> championPlayCounts,
    String profileIconUrl,
    String errorMessage // Optional: to transport error details from service to controller
) {
    // Constructor for success case
    public SummonerProfileData(Summoner summoner, List<LeagueEntryDTO> leagueEntries, List<MatchV5Dto> matchHistory, SummonerSuggestionDTO suggestion, Map<String, Long> championPlayCounts, String profileIconUrl) {
        this(summoner, leagueEntries, matchHistory, suggestion, championPlayCounts, profileIconUrl, null);
    }

    // Constructor for error case
    public SummonerProfileData(String errorMessage) {
        this(null, List.of(), List.of(), null, Map.of(), null, errorMessage);
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.isEmpty();
    }
}
