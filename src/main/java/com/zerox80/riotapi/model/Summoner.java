package com.zerox80.riotapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Model class representing a League of Legends summoner (player).
 * 
 * <p>This class encapsulates basic summoner information retrieved from the Riot Games API,
 * including various identifiers, display name, profile icon, and account details.
 * This information is used for player identification and profile display throughout the application.</p>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Summoner {

    /**
     * The encrypted summoner ID.
     * 
     * <p>This is a unique identifier for the summoner that is specific to the region
     * and platform. It is used for most API calls that require summoner identification.
     * This ID is encrypted and should not be assumed to be stable across API versions.</p>
     */
    private String id;
    
    /**
     * The encrypted account ID.
     * 
     * <p>This identifier is associated with the player's Riot account and is used
     * for some legacy API endpoints. It is region-specific and encrypted.
     * Note: This field is being deprecated in favor of PUUID.</p>
     */
    private String accountId;
    
    /**
     * The Player Universally Unique Identifier (PUUID).
     * 
     * <p>This is a globally unique identifier that is consistent across all Riot Games
     * and regions for the same player. It is the preferred identifier for cross-game
     * and cross-region player identification and data correlation.</p>
     */
    private String puuid;
    
    /**
     * The summoner name.
     * 
     * <p>This is the display name that players see in-game and can be changed
     * by the player. Names are not unique and may contain special characters.
     * This name is localized to the region where the summoner was created.</p>
     */
    private String name;
    
    /**
     * The profile icon ID.
     * 
     * <p>This numeric ID corresponds to a specific profile icon image from Data Dragon.
     * The full URL for the profile icon can be constructed using this ID along
     * with the Data Dragon base URL for profile icons.</p>
     */
    private int profileIconId;
    
    /**
     * The date when the summoner was last modified.
     * 
     * <p>This timestamp represents the last time any of the summoner's data
     * was changed, including name changes, level ups, or other profile updates.
     * The value is in epoch milliseconds format.</p>
     */
    private long revisionDate;
    
    /**
     * The summoner's current level.
     * 
     * <p>This represents the summoner's account level, which increases through
     * playing games and earning experience. The maximum level is currently
     * unlimited, with special milestones at certain levels.</p>
     */
    private long summonerLevel;

}
