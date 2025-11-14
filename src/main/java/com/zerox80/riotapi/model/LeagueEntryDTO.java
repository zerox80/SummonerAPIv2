// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Lombok Data annotation to auto-generate getters, setters, equals, hashCode, toString
import lombok.Data;
// Import for Lombok NoArgsConstructor annotation to auto-generate no-argument constructor
import lombok.NoArgsConstructor;
// Import for Lombok AllArgsConstructor annotation to auto-generate all-arguments constructor
import lombok.AllArgsConstructor;


/**
 * Data model representing a player's ranked league entry from Riot's League-V4 API.
 * Contains detailed ranked statistics and standing information for a specific queue.
 *
 * This model maps to the League Entry API response and includes the player's current
 * tier (e.g., GOLD), rank (e.g., II), LP (League Points), wins, losses, and various
 * status flags. Each player can have multiple league entries for different queue types
 * (Solo/Duo, Flex, etc.).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeagueEntryDTO {

    // The unique identifier for the league this entry belongs to
    private String leagueId;


    // The summoner's unique ID (encrypted summoner ID, not PUUID)
    private String summonerId;


    // The summoner's name at the time of the API call (legacy field)
    private String summonerName;


    // The queue type (e.g., "RANKED_SOLO_5x5", "RANKED_FLEX_SR")
    private String queueType;


    // The tier/division (e.g., "GOLD", "PLATINUM", "DIAMOND")
    private String tier;


    // The rank within the tier (e.g., "I", "II", "III", "IV")
    private String rank;


    // Current League Points (LP) in this rank
    private int leaguePoints;


    // Total number of wins in this queue
    private int wins;


    // Total number of losses in this queue
    private int losses;


    // LP gained per win (may be null if not available)
    private Integer lpGain;


    // LP lost per loss (may be null if not available)
    private Integer lpLoss;


    // Indicates if the player is a veteran (has played 100+ games in this tier)
    private boolean veteran;


    // Indicates if the player is inactive (hasn't played recently)
    private boolean inactive;


    // Indicates if the player recently promoted to this tier
    private boolean freshBlood;


    // Indicates if the player is on a hot streak (3+ consecutive wins)
    private boolean hotStreak;
}
