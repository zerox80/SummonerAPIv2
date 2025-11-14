// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Lombok Data annotation to auto-generate getters, setters, equals, hashCode, toString
import lombok.Data;
// Import for Lombok NoArgsConstructor annotation to auto-generate no-argument constructor
import lombok.NoArgsConstructor;
// Import for Lombok AllArgsConstructor annotation to auto-generate all-arguments constructor
import lombok.AllArgsConstructor;


/**
 * Data model representing a League of Legends summoner from Riot's Summoner-V4 API.
 * Contains core summoner information and identifiers.
 *
 * This model maps to the Summoner API response and includes various identifiers
 * (encrypted summoner ID, account ID, PUUID), profile information (icon ID, level),
 * and metadata (revision date). Used as the primary summoner data object when
 * fetching player information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Summoner {

    // Encrypted summoner ID (used for League-V4 API calls)
    private String id;


    // Encrypted account ID (legacy identifier, mostly deprecated)
    private String accountId;


    // Player's unique universal identifier (PUUID) used across all Riot games
    private String puuid;


    // Summoner name (legacy field, use Riot ID from Account API instead for new code)
    private String name;


    // Profile icon ID (used to display the player's profile picture)
    private int profileIconId;


    // Unix timestamp (milliseconds) when this summoner was last modified
    private long revisionDate;


    // The summoner's current level (no level cap in modern League of Legends)
    private long summonerLevel;

}
