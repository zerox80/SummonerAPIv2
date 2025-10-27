package com.zerox80.riotapi.dto;

/**
 * Data Transfer Object representing rune configuration statistics for a champion.
 * 
 * <p>This class encapsulates performance metrics for specific rune setups when used
 * with a particular champion, including primary and secondary rune paths, keystone choice,
 * and associated win/pick statistics. This data is used to generate optimal rune recommendations.</p>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class RuneStatDto {
    /**
     * The ID of the primary rune path.
     * 
     * <p>This identifies the main rune tree (e.g., Precision, Domination, Sorcery,
     * Resolve, Inspiration) that the player chose as their primary path.</p>
     */
    private int primaryStyle;
    
    /**
     * The ID of the secondary rune path.
     * 
     * <p>This identifies the secondary rune tree that the player chose
     * to complement their primary path selection.</p>
     */
    private int subStyle;
    
    /**
     * The ID of the keystone rune.
     * 
     * <p>This identifies the specific keystone rune chosen from the primary path,
     * which is typically the most impactful rune in the setup.</p>
     */
    private int keystone;
    
    /**
     * The number of games this rune configuration was used.
     * 
     * <p>This count represents the total number of games where players used
     * this specific rune setup with the specified champion.</p>
     */
    private int count;
    
    /**
     * The number of games won with this rune configuration.
     * 
     * <p>This represents the number of victories when using this rune setup,
     * used to calculate win rate statistics for the configuration.</p>
     */
    private int wins;
    
    /**
     * The localized name of the primary rune path.
     * 
     * <p>This is the display name for the primary rune tree,
     * localized for the appropriate region.</p>
     */
    private String primaryStyleName;
    
    /**
     * The localized name of the secondary rune path.
     * 
     * <p>This is the display name for the secondary rune tree,
     * localized for the appropriate region.</p>
     */
    private String subStyleName;
    
    /**
     * The localized name of the keystone rune.
     * 
     * <p>This is the display name for the keystone rune,
     * localized for the appropriate region.</p>
     */
    private String keystoneName;
    
    /**
     * The URL for the keystone rune's icon image.
     * 
     * <p>This URL points to the keystone's icon image from Data Dragon,
     * used for visual display in rune recommendations.</p>
     */
    private String keystoneIconUrl;

    /**
     * Constructs a new RuneStatDto with the specified rune statistics.
     * 
     * @param primaryStyle The ID of the primary rune path
     * @param subStyle The ID of the secondary rune path
     * @param keystone The ID of the keystone rune
     * @param count The number of games this configuration was used
     * @param wins The number of games won with this configuration
     * @param primaryStyleName The localized name of the primary path
     * @param subStyleName The localized name of the secondary path
     * @param keystoneName The localized name of the keystone
     * @param keystoneIconUrl The URL for the keystone's icon image
     */
    public RuneStatDto(int primaryStyle, int subStyle, int keystone, int count, int wins,
                       String primaryStyleName, String subStyleName, String keystoneName, String keystoneIconUrl) {
        this.primaryStyle = primaryStyle;
        this.subStyle = subStyle;
        this.keystone = keystone;
        this.count = count;
        this.wins = wins;
        this.primaryStyleName = primaryStyleName;
        this.subStyleName = subStyleName;
        this.keystoneName = keystoneName;
        this.keystoneIconUrl = keystoneIconUrl;
    }

    /**
     * Gets the ID of the primary rune path.
     * 
     * @return The primary style ID
     */
    public int getPrimaryStyle() { return primaryStyle; }
    
    /**
     * Gets the ID of the secondary rune path.
     * 
     * @return The secondary style ID
     */
    public int getSubStyle() { return subStyle; }
    
    /**
     * Gets the ID of the keystone rune.
     * 
     * @return The keystone ID
     */
    public int getKeystone() { return keystone; }
    
    /**
     * Gets the number of games this rune configuration was used.
     * 
     * @return The usage count
     */
    public int getCount() { return count; }
    
    /**
     * Gets the number of games won with this rune configuration.
     * 
     * @return The win count
     */
    public int getWins() { return wins; }
    
    /**
     * Gets the localized name of the primary rune path.
     * 
     * @return The primary style name
     */
    public String getPrimaryStyleName() { return primaryStyleName; }
    
    /**
     * Gets the localized name of the secondary rune path.
     * 
     * @return The secondary style name
     */
    public String getSubStyleName() { return subStyleName; }
    
    /**
     * Gets the localized name of the keystone rune.
     * 
     * @return The keystone name
     */
    public String getKeystoneName() { return keystoneName; }
    
    /**
     * Gets the URL for the keystone rune's icon image.
     * 
     * @return The keystone icon URL
     */
    public String getKeystoneIconUrl() { return keystoneIconUrl; }
}
