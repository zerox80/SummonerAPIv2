package com.zerox80.riotapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Data Transfer Object representing a summoner's league entry information.
 * 
 * <p>This class encapsulates ranked statistics and information for a summoner
 * in a specific competitive queue (e.g., ranked solo/duo, ranked flex).
 * It includes rank, tier, win/loss records, and various status flags that
 * indicate special conditions or achievements.</p>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeagueEntryDTO {
    /**
     * The unique identifier for the league.
     * 
     * <p>This ID identifies the specific league (e.g., challenger, grandmaster, master)
     * that this entry belongs to. It is used for league-related API calls.</p>
     */
    private String leagueId;
    
    /**
     * The encrypted summoner ID for this league entry.
     * 
     * <p>This identifies the summoner who owns this league entry and can be used
     * to retrieve additional summoner information from other API endpoints.</p>
     */
    private String summonerId;
    
    /**
     * The summoner name at the time of this league entry.
     * 
     * <p>This represents the summoner's name when they were active in this league.
     * Note that this may not match the summoner's current name if they have changed it.</p>
     */
    private String summonerName;
    
    /**
     * The type of ranked queue this entry belongs to.
     * 
     * <p>Common values include "RANKED_SOLO_5x5" for solo/duo queue and
     * "RANKED_FLEX_SR" for flex queue. This determines the game mode and team size.</p>
     */
    private String queueType;
    
    /**
     * The competitive tier of the summoner.
     * 
     * <p>This represents the major rank division (e.g., "IRON", "BRONZE", "SILVER",
     * "GOLD", "PLATINUM", "DIAMOND", "MASTER", "GRANDMASTER", "CHALLENGER").</p>
     */
    private String tier;
    
    /**
     * The rank within the tier.
     * 
     * <p>This represents the subdivision within the tier (e.g., "I", "II", "III", "IV").
     * For master tier and above, this value is typically "I" as there are no subdivisions.</p>
     */
    private String rank;
    
    /**
     * The current league points (LP) for the summoner.
     * 
     * <p>This represents the LP within the current rank. Values typically range
     * from 0-99 for regular tiers, while master tier and above can have unlimited LP.</p>
     */
    private int leaguePoints;
    
    /**
     * The number of wins in this ranked queue.
     * 
     * <p>This counts all victories the summoner has achieved in this specific
     * competitive queue during the current season.</p>
     */
    private int wins;
    
    /**
     * The number of losses in this ranked queue.
     * 
     * <p>This counts all defeats the summoner has suffered in this specific
     * competitive queue during the current season.</p>
     */
    private int losses;
    
    /**
     * The LP gain for the next victory.
     * 
     * <p>This indicates how many LP will be gained on the next win. This value
     * is calculated based on MMR and can vary. May be null if not available.</p>
     */
    private Integer lpGain;
    
    /**
     * The LP loss for the next defeat.
     * 
     * <p>This indicates how many LP will be lost on the next loss. This value
     * is calculated based on MMR and can vary. May be null if not available.</p>
     */
    private Integer lpLoss;
    
    /**
     * Indicates if the summoner is a veteran player.
     * 
     * <p>This flag is set to true for players who have been in the same tier
     * for a long time, typically 100+ games. It indicates experience and stability.</p>
     */
    private boolean veteran;
    
    /**
     * Indicates if the summoner is inactive.
     * 
     * <p>This flag is set to true for players who haven't played ranked games
     * recently (typically 28 days). Inactive players may be hidden from leaderboards.</p>
     */
    private boolean inactive;
    
    /**
     * Indicates if the summoner is fresh blood.
     * 
     * <p>This flag is set to true for players who recently joined this tier
     * and are performing well. It indicates rising talent in the division.</p>
     */
    private boolean freshBlood;
    
    /**
     * Indicates if the summoner is on a hot streak.
     * 
     * <p>This flag is set to true for players who have won multiple games
     * in a row recently. It indicates current strong performance.</p>
     */
    private boolean hotStreak;
} 
