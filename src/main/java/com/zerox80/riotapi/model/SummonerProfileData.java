package com.zerox80.riotapi.model;

import java.util.List;
import java.util.Map;

/**
 * A record that encapsulates all data related to a summoner's profile.
 *
 * <p>This record is a container for various pieces of information that make up
 * a complete summoner profile, including the summoner's basic data, their
 * league entries, match history, and champion play counts. It also includes
 * an optional error message for graceful error handling.</p>
 *
 * @param summoner The summoner's basic data.
 * @param leagueEntries A list of the summoner's league entries (ranked stats).
 * @param matchHistory A list of the summoner's recent matches.
 * @param suggestion A summoner suggestion DTO.
 * @param championPlayCounts A map of champion IDs to their play counts.
 * @param profileIconUrl The URL of the summoner's profile icon.
 * @param errorMessage An optional error message.
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public record SummonerProfileData(
    Summoner summoner,
    List<LeagueEntryDTO> leagueEntries,
    List<MatchV5Dto> matchHistory,
    SummonerSuggestionDTO suggestion,
    Map<String, Long> championPlayCounts,
    String profileIconUrl,
    String errorMessage // Optional: to transport error details from service to controller
) {
    /**
     * Constructor for a successful summoner profile data retrieval.
     *
     * @param summoner The summoner's basic data.
     * @param leagueEntries A list of the summoner's league entries.
     * @param matchHistory A list of the summoner's recent matches.
     * @param suggestion A summoner suggestion DTO.
     * @param championPlayCounts A map of champion IDs to play counts.
     * @param profileIconUrl The URL of the summoner's profile icon.
     */
    public SummonerProfileData(Summoner summoner, List<LeagueEntryDTO> leagueEntries, List<MatchV5Dto> matchHistory, SummonerSuggestionDTO suggestion, Map<String, Long> championPlayCounts, String profileIconUrl) {
        this(summoner, leagueEntries, matchHistory, suggestion, championPlayCounts, profileIconUrl, null);
    }

    /**
     * Constructor for a failed summoner profile data retrieval.
     *
     * @param errorMessage The error message.
     */
    public SummonerProfileData(String errorMessage) {
        this(null, List.of(), List.of(), null, Map.of(), null, errorMessage);
    }

    /**
     * Checks if the profile data contains an error message.
     *
     * @return True if there is an error message, false otherwise.
     */
    public boolean hasError() {
        return errorMessage != null && !errorMessage.isEmpty();
    }
}
