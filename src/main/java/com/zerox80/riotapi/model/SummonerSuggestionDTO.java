package com.zerox80.riotapi.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for summoner search suggestions.
 *
 * <p>This class encapsulates the information needed to display a summoner
 * as a suggestion in a search autocomplete feature. It includes the summoner's
 * Riot ID, profile icon, and level.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummonerSuggestionDTO {
    /** The summoner's Riot ID (Name#TAG). */
    private String riotId;
    /** The ID of the summoner's profile icon. */
    private int profileIconId;
    /** The summoner's level. */
    private long summonerLevel;
    /** The URL of the summoner's profile icon. */
    private String profileIconUrl;

    /**
     * Constructs a new SummonerSuggestionDTO with the specified details.
     *
     * @param riotId The summoner's Riot ID
     * @param profileIconId The ID of the summoner's profile icon
     * @param summonerLevel The summoner's level
     */
    public SummonerSuggestionDTO(String riotId, int profileIconId, long summonerLevel) {
        this.riotId = riotId;
        this.profileIconId = profileIconId;
        this.summonerLevel = summonerLevel;
        this.profileIconUrl = null;
    }
}
