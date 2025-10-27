package com.zerox80.riotapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a Riot Games account.
 * 
 * <p>This class encapsulates account information retrieved from the Riot Account API,
 * including the player's universally unique identifier (PUUID), game name, and tag line.
 * This information is used to identify players across all Riot Games titles.</p>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Data
@NoArgsConstructor
public class AccountDto {
    /**
     * The Player Universally Unique Identifier (PUUID) for the account.
     * 
     * <p>This is a unique identifier that is consistent across all Riot Games
     * and regions for the same player. It is used as the primary key for
     * player identification and data correlation.</p>
     */
    private String puuid;
    
    /**
     * The game name portion of the player's Riot ID.
     * 
     * <p>This is the display name that players choose for their account.
     * It can be changed by the player and may not be unique across all players.</p>
     */
    private String gameName;
    
    /**
     * The tag line portion of the player's Riot ID.
     * 
     * <p>This is a unique identifier combined with the game name to form the
     * complete Riot ID (e.g., "GameName#TagLine"). Tag lines ensure uniqueness
     * across all players in a region.</p>
     */
    private String tagLine;
} 
