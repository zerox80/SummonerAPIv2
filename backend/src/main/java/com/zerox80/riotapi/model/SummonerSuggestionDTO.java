// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Lombok Data annotation to auto-generate getters, setters, equals, hashCode, toString
import lombok.Data;
// Import for Lombok AllArgsConstructor annotation to auto-generate all-arguments constructor
import lombok.AllArgsConstructor;
// Import for Lombok NoArgsConstructor annotation to auto-generate no-argument constructor
import lombok.NoArgsConstructor;


/**
 * Data Transfer Object for summoner search suggestions/autocomplete.
 * Contains minimal summoner information for search result display.
 *
 * This DTO is used in autocomplete/typeahead search features where users
 * type a summoner name and see suggestions. Includes the Riot ID (game name + tag),
 * profile icon, level, and a pre-generated icon URL for efficient display.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummonerSuggestionDTO {

    // The Riot ID in "GameName#TAG" format (e.g., "PlayerName#NA1")
    private String riotId;

    // Profile icon ID (used to construct icon URL if profileIconUrl is null)
    private int profileIconId;

    // The summoner's level
    private long summonerLevel;

    // Pre-generated URL to the profile icon image (may be null)
    private String profileIconUrl;

    /**
     * Constructs a summoner suggestion without a pre-generated icon URL.
     * The profileIconUrl will be set to null and can be generated later.
     *
     * @param riotId The Riot ID
     * @param profileIconId The profile icon ID
     * @param summonerLevel The summoner level
     */
    public SummonerSuggestionDTO(String riotId, int profileIconId, long summonerLevel) {
        this.riotId = riotId;
        this.profileIconId = profileIconId;
        this.summonerLevel = summonerLevel;
        this.profileIconUrl = null;
    }
}
