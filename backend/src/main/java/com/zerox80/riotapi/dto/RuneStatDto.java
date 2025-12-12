// Package declaration: Defines that this class belongs to the dto (Data Transfer Object) package
package com.zerox80.riotapi.dto;


/**
 * Data Transfer Object for champion rune configuration statistics.
 * Represents usage and performance data for a specific rune setup on a champion.
 *
 * This DTO is included in ChampionBuildDto responses and provides information about
 * popular rune combinations including the primary rune tree, secondary rune tree,
 * and keystone rune. It includes both IDs for data processing and display names/images
 * for the frontend.
 */
public class RuneStatDto {

    // The primary rune tree ID (e.g., 8000 for Precision, 8100 for Domination)
    private int primaryStyle;


    // The secondary rune tree ID (different from primary)
    private int subStyle;


    // The keystone rune ID (e.g., 8010 for Conqueror, 8005 for Press the Attack)
    private int keystone;


    // The number of games this rune configuration was used (usage count)
    private int count;


    // The number of games won with this rune configuration (for calculating win rate)
    private int wins;


    // The display name of the primary rune tree (e.g., "Precision")
    private String primaryStyleName;


    // The display name of the secondary rune tree (e.g., "Domination")
    private String subStyleName;


    // The display name of the keystone rune (e.g., "Conqueror")
    private String keystoneName;


    // The URL to the keystone rune's icon image for display in the UI
    private String keystoneIconUrl;

    /**
     * Constructs a rune statistic DTO with usage data and display information.
     *
     * @param primaryStyle The primary rune tree ID
     * @param subStyle The secondary rune tree ID
     * @param keystone The keystone rune ID
     * @param count Total number of games with this rune configuration
     * @param wins Number of games won with this rune configuration
     * @param primaryStyleName The primary rune tree's display name
     * @param subStyleName The secondary rune tree's display name
     * @param keystoneName The keystone rune's display name
     * @param keystoneIconUrl URL to the keystone rune's icon image
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
     * Gets the primary rune tree ID.
     *
     * @return The primary style ID
     */
    public int getPrimaryStyle() { return primaryStyle; }

    /**
     * Gets the secondary rune tree ID.
     *
     * @return The secondary style ID
     */
    public int getSubStyle() { return subStyle; }

    /**
     * Gets the keystone rune ID.
     *
     * @return The keystone ID
     */
    public int getKeystone() { return keystone; }

    /**
     * Gets the usage count (total games with this rune configuration).
     *
     * @return The number of games
     */
    public int getCount() { return count; }

    /**
     * Gets the win count (games won with this rune configuration).
     *
     * @return The number of wins
     */
    public int getWins() { return wins; }

    /**
     * Gets the primary rune tree's display name.
     *
     * @return The primary style name
     */
    public String getPrimaryStyleName() { return primaryStyleName; }

    /**
     * Gets the secondary rune tree's display name.
     *
     * @return The secondary style name
     */
    public String getSubStyleName() { return subStyleName; }

    /**
     * Gets the keystone rune's display name.
     *
     * @return The keystone name
     */
    public String getKeystoneName() { return keystoneName; }

    /**
     * Gets the URL to the keystone rune's icon image.
     *
     * @return The keystone icon URL
     */
    public String getKeystoneIconUrl() { return keystoneIconUrl; }
}
