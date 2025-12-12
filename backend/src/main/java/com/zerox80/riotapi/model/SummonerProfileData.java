// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Java List collection to hold league entries and match history
import java.util.List;
// Import for Java Map to hold champion play counts
import java.util.Map;


/**
 * Java Record representing complete summoner profile data for display.
 * Aggregates all information needed to render a summoner profile page.
 *
 * This record combines data from multiple Riot APIs (Summoner, League, Match) into a single
 * cohesive data structure. Includes summoner basic info, ranked standings, recent match history,
 * search suggestion data, champion statistics, profile icon URL, and optional error messages.
 *
 * As a Java record, this class is immutable and automatically provides constructors,
 * getters, equals, hashCode, and toString methods.
 *
 * @param summoner The summoner basic information (name, level, etc.)
 * @param leagueEntries List of ranked league entries (Solo/Duo, Flex)
 * @param matchHistory List of recent matches with full details
 * @param suggestion Suggestion data for autocomplete/search features
 * @param championPlayCounts Map of champion IDs to number of games played
 * @param profileIconUrl URL to the summoner's profile icon image
 * @param errorMessage Optional error message if profile fetch failed, null on success
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
     * Constructs a successful summoner profile without an error message.
     * The errorMessage will be null, indicating success.
     *
     * @param summoner The summoner basic information
     * @param leagueEntries List of ranked league entries
     * @param matchHistory List of recent matches
     * @param suggestion Suggestion data for search
     * @param championPlayCounts Map of champion play counts
     * @param profileIconUrl URL to profile icon
     */
    public SummonerProfileData(Summoner summoner, List<LeagueEntryDTO> leagueEntries, List<MatchV5Dto> matchHistory, SummonerSuggestionDTO suggestion, Map<String, Long> championPlayCounts, String profileIconUrl) {
        this(summoner, leagueEntries, matchHistory, suggestion, championPlayCounts, profileIconUrl, null);
    }

    /**
     * Constructs an error profile with only an error message.
     * All other fields are set to null or empty collections.
     *
     * @param errorMessage The error message explaining what went wrong
     */
    public SummonerProfileData(String errorMessage) {
        this(null, List.of(), List.of(), null, Map.of(), null, errorMessage);
    }

    /**
     * Checks if this profile represents an error state.
     *
     * @return true if an error occurred, false if the profile loaded successfully
     */
    public boolean hasError() {
        return errorMessage != null && !errorMessage.isEmpty();
    }
}
