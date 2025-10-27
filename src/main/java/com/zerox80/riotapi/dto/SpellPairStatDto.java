package com.zerox80.riotapi.dto;

/**
 * Data Transfer Object representing summoner spell pair statistics for a champion.
 * 
 * <p>This class encapsulates performance metrics for specific combinations of summoner spells
 * when used with a particular champion, including pick count, win count, and display information.
 * This data is used to generate optimal summoner spell recommendations based on aggregated match data.</p>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class SpellPairStatDto {
    /**
     * The ID of the first summoner spell in the pair.
     * 
     * <p>This identifies the first spell (typically the D spell) in the summoner spell
     * combination. Spell IDs are consistent across all regions and game versions.</p>
     */
    private int spell1Id;
    
    /**
     * The ID of the second summoner spell in the pair.
     * 
     * <p>This identifies the second spell (typically the F spell) in the summoner spell
     * combination. The order is maintained for consistency with API responses.</p>
     */
    private int spell2Id;
    
    /**
     * The number of games this spell pair was used.
     * 
     * <p>This count represents the total number of games where players used
     * this specific combination of summoner spells with the specified champion.</p>
     */
    private int count;
    
    /**
     * The number of games won with this spell pair.
     * 
     * <p>This represents the number of victories when using this spell combination,
     * used to calculate win rate statistics for the pair.</p>
     */
    private int wins;
    
    /**
     * The localized name of the first summoner spell.
     * 
     * <p>This is the display name for the first spell in the pair,
     * localized for the appropriate region (e.g., "Flash", "Ignite", "Heal").</p>
     */
    private String spell1Name;
    
    /**
     * The localized name of the second summoner spell.
     * 
     * <p>This is the display name for the second spell in the pair,
     * localized for the appropriate region.</p>
     */
    private String spell2Name;
    
    /**
     * The URL for the first spell's icon image.
     * 
     * <p>This URL points to the first spell's icon image from Data Dragon,
     * used for visual display in spell recommendations.</p>
     */
    private String spell1IconUrl;
    
    /**
     * The URL for the second spell's icon image.
     * 
     * <p>This URL points to the second spell's icon image from Data Dragon,
     * used for visual display in spell recommendations.</p>
     */
    private String spell2IconUrl;

    /**
     * Constructs a new SpellPairStatDto with the specified spell pair statistics.
     * 
     * @param spell1Id The ID of the first summoner spell
     * @param spell2Id The ID of the second summoner spell
     * @param count The number of games this spell pair was used
     * @param wins The number of games won with this spell pair
     * @param spell1Name The localized name of the first spell
     * @param spell2Name The localized name of the second spell
     * @param spell1IconUrl The URL for the first spell's icon image
     * @param spell2IconUrl The URL for the second spell's icon image
     */
    public SpellPairStatDto(int spell1Id, int spell2Id, int count, int wins,
                            String spell1Name, String spell2Name,
                            String spell1IconUrl, String spell2IconUrl) {
        this.spell1Id = spell1Id;
        this.spell2Id = spell2Id;
        this.count = count;
        this.wins = wins;
        this.spell1Name = spell1Name;
        this.spell2Name = spell2Name;
        this.spell1IconUrl = spell1IconUrl;
        this.spell2IconUrl = spell2IconUrl;
    }

    /**
     * Gets the ID of the first summoner spell in the pair.
     * 
     * @return The first spell ID
     */
    public int getSpell1Id() { return spell1Id; }
    
    /**
     * Gets the ID of the second summoner spell in the pair.
     * 
     * @return The second spell ID
     */
    public int getSpell2Id() { return spell2Id; }
    
    /**
     * Gets the number of games this spell pair was used.
     * 
     * @return The usage count
     */
    public int getCount() { return count; }
    
    /**
     * Gets the number of games won with this spell pair.
     * 
     * @return The win count
     */
    public int getWins() { return wins; }
    
    /**
     * Gets the localized name of the first summoner spell.
     * 
     * @return The first spell name
     */
    public String getSpell1Name() { return spell1Name; }
    
    /**
     * Gets the localized name of the second summoner spell.
     * 
     * @return The second spell name
     */
    public String getSpell2Name() { return spell2Name; }
    
    /**
     * Gets the URL for the first spell's icon image.
     * 
     * @return The first spell icon URL
     */
    public String getSpell1IconUrl() { return spell1IconUrl; }
    
    /**
     * Gets the URL for the second spell's icon image.
     * 
     * @return The second spell icon URL
     */
    public String getSpell2IconUrl() { return spell2IconUrl; }
}
