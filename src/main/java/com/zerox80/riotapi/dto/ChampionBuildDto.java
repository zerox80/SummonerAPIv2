package com.zerox80.riotapi.dto;

import java.util.List;

/**
 * Data Transfer Object representing aggregated champion build statistics.
 * 
 * <p>This class encapsulates build recommendations and statistics for a specific champion,
 * including optimal items, runes, and summoner spells based on aggregated match data.
 * The statistics are typically filtered by patch, queue type, and role for more targeted recommendations.</p>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class ChampionBuildDto {
    /**
     * The champion ID for which these build statistics apply.
     * 
     * <p>This is typically the champion's key identifier (e.g., "Ahri", "Yasuo")
     * used by Data Dragon and other Riot APIs.</p>
     */
    private String championId;
    
    /**
     * The game patch version for which these statistics are relevant.
     * 
     * <p>This ensures that build recommendations are appropriate for the current
     * game version, as item and rune effectiveness can change significantly between patches.</p>
     */
    private String patch;
    
    /**
     * The queue ID representing the game mode/ranked type.
     * 
     * <p>Common values include 420 for Summoner's Rift ranked solo/duo,
     * 440 for ranked flex, and 450 for ARAM. Build strategies can vary
     * significantly between different queue types.</p>
     */
    private int queueId;
    
    /**
     * The role filter used when generating these statistics.
     * 
     * <p>This is an optional field that specifies the champion role (e.g., "TOP", "JUNGLE", "MID", "ADC", "SUPPORT")
     * for which these build recommendations are optimized. If null, the statistics include all roles.</p>
     */
    private String role; // optional filter used
    
    /**
     * List of item statistics showing the most effective items for this champion.
     * 
     * <p>Each ItemStatDto contains information about an item's win rate, pick rate,
     * and other performance metrics when used with this champion in the specified context.</p>
     */
    private List<ItemStatDto> items;
    
    /**
     * List of rune statistics showing the most effective rune configurations.
     * 
     * <p>Each RuneStatDto contains information about rune choices, including primary
     * and secondary paths, with associated win rates and pick rates.</p>
     */
    private List<RuneStatDto> runes;
    
    /**
     * List of summoner spell pair statistics showing optimal spell combinations.
     * 
     * <p>Each SpellPairStatDto contains information about the effectiveness of
     * different summoner spell combinations when used with this champion.</p>
     */
    private List<SpellPairStatDto> spells;

    /**
     * Constructs a new ChampionBuildDto with the specified build statistics.
     * 
     * @param championId The champion ID for these build statistics
     * @param patch The game patch version
     * @param queueId The queue ID representing game mode
     * @param role The role filter (optional)
     * @param items List of item statistics
     * @param runes List of rune statistics  
     * @param spells List of summoner spell statistics
     */
    public ChampionBuildDto(String championId, String patch, int queueId, String role,
                            List<ItemStatDto> items, List<RuneStatDto> runes, List<SpellPairStatDto> spells) {
        this.championId = championId;
        this.patch = patch;
        this.queueId = queueId;
        this.role = role;
        this.items = items;
        this.runes = runes;
        this.spells = spells;
    }

    /**
     * Gets the champion ID for these build statistics.
     * 
     * @return The champion ID
     */
    public String getChampionId() { return championId; }
    
    /**
     * Gets the game patch version.
     * 
     * @return The patch version
     */
    public String getPatch() { return patch; }
    
    /**
     * Gets the queue ID representing the game mode.
     * 
     * @return The queue ID
     */
    public int getQueueId() { return queueId; }
    
    /**
     * Gets the role filter used for these statistics.
     * 
     * @return The role filter, or null if no role filter was applied
     */
    public String getRole() { return role; }
    
    /**
     * Gets the list of item statistics.
     * 
     * @return List of item statistics
     */
    public List<ItemStatDto> getItems() { return items; }
    
    /**
     * Gets the list of rune statistics.
     * 
     * @return List of rune statistics
     */
    public List<RuneStatDto> getRunes() { return runes; }
    
    /**
     * Gets the list of summoner spell statistics.
     * 
     * @return List of summoner spell statistics
     */
    public List<SpellPairStatDto> getSpells() { return spells; }
}
