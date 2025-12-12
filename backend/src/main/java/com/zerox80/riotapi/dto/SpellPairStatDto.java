// Package declaration: Defines that this class belongs to the dto (Data Transfer Object) package
package com.zerox80.riotapi.dto;


/**
 * Data Transfer Object for champion summoner spell pair statistics.
 * Represents usage and performance data for a specific summoner spell combination on a champion.
 *
 * This DTO is included in ChampionBuildDto responses and provides information about
 * popular summoner spell pairs (e.g., Flash + Ignite, Flash + Teleport). It includes
 * both IDs for data processing and display names/images for the frontend.
 */
public class SpellPairStatDto {

    // The first summoner spell ID (e.g., 4 for Flash)
    private int spell1Id;


    // The second summoner spell ID (e.g., 14 for Ignite, 12 for Teleport)
    private int spell2Id;


    // The number of games this spell combination was used (usage count)
    private int count;


    // The number of games won with this spell combination (for calculating win rate)
    private int wins;


    // The display name of the first summoner spell (e.g., "Flash")
    private String spell1Name;


    // The display name of the second summoner spell (e.g., "Ignite")
    private String spell2Name;


    // The URL to the first summoner spell's icon image for display in the UI
    private String spell1IconUrl;


    // The URL to the second summoner spell's icon image for display in the UI
    private String spell2IconUrl;

    /**
     * Constructs a summoner spell pair statistic DTO with usage data and display information.
     *
     * @param spell1Id The first summoner spell ID
     * @param spell2Id The second summoner spell ID
     * @param count Total number of games with this spell pair
     * @param wins Number of games won with this spell pair
     * @param spell1Name The first summoner spell's display name
     * @param spell2Name The second summoner spell's display name
     * @param spell1IconUrl URL to the first summoner spell's icon image
     * @param spell2IconUrl URL to the second summoner spell's icon image
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
     * Gets the first summoner spell ID.
     *
     * @return The first spell ID
     */
    public int getSpell1Id() { return spell1Id; }

    /**
     * Gets the second summoner spell ID.
     *
     * @return The second spell ID
     */
    public int getSpell2Id() { return spell2Id; }

    /**
     * Gets the usage count (total games with this spell pair).
     *
     * @return The number of games
     */
    public int getCount() { return count; }

    /**
     * Gets the win count (games won with this spell pair).
     *
     * @return The number of wins
     */
    public int getWins() { return wins; }

    /**
     * Gets the first summoner spell's display name.
     *
     * @return The first spell name
     */
    public String getSpell1Name() { return spell1Name; }

    /**
     * Gets the second summoner spell's display name.
     *
     * @return The second spell name
     */
    public String getSpell2Name() { return spell2Name; }

    /**
     * Gets the URL to the first summoner spell's icon image.
     *
     * @return The first spell icon URL
     */
    public String getSpell1IconUrl() { return spell1IconUrl; }

    /**
     * Gets the URL to the second summoner spell's icon image.
     *
     * @return The second spell icon URL
     */
    public String getSpell2IconUrl() { return spell2IconUrl; }
}
