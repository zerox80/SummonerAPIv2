// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Lombok Data annotation to auto-generate getters, setters, equals, hashCode, toString
import lombok.Data;
// Import for Lombok NoArgsConstructor annotation to auto-generate no-argument constructor
import lombok.NoArgsConstructor;


/**
 * Data model representing a Riot Games account.
 * Maps to the Riot Account API v1 response structure.
 *
 * This model contains the core account identifiers returned by the Riot Account API,
 * including the PUUID (universal player identifier) and the Riot ID components
 * (game name and tag line). Used for account lookups and player identification.
 */
@Data
@NoArgsConstructor
public class AccountDto {

    // The player's unique universal identifier (PUUID) used across all Riot games
    private String puuid;


    // The player's in-game display name (e.g., "PlayerName")
    private String gameName;


    // The player's Riot ID tag/suffix (e.g., "NA1" in "PlayerName#NA1")
    private String tagLine;
}
