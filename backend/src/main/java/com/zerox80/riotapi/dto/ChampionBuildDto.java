// Package declaration: Defines that this class belongs to the dto (Data Transfer Object) package
package com.zerox80.riotapi.dto;

// Import for Java List collection to hold multiple build statistics
import java.util.List;


/**
 * Data Transfer Object for champion build recommendations.
 * Aggregates all build-related statistics (items, runes, summoner spells) for a specific champion.
 *
 * This DTO is returned by the build API endpoint and provides comprehensive build information
 * filtered by patch version, queue type, and optionally role. It combines data from multiple
 * database entities into a single cohesive response for the frontend.
 */
public class ChampionBuildDto {

    // The unique identifier for the champion (e.g., "266" for Aatrox)
    private String championId;


    // The game patch version these statistics apply to (e.g., "15.18")
    private String patch;


    // The queue type identifier (420 for Solo/Duo Ranked, 440 for Flex Ranked)
    private int queueId;


    // The lane role filter applied (TOP, JUNGLE, MID, ADC, SUPPORT), or null if showing all roles
    private String role; // optional filter used


    // List of popular item build statistics for this champion, ordered by usage count
    private List<ItemStatDto> items;


    // List of popular rune configuration statistics for this champion, ordered by usage count
    private List<RuneStatDto> runes;


    // List of popular summoner spell pair statistics for this champion, ordered by usage count
    private List<SpellPairStatDto> spells;

    /**
     * Constructs a complete champion build DTO with all statistics.
     *
     * @param championId The champion identifier
     * @param patch The patch version
     * @param queueId The queue type
     * @param role The role filter (can be null for all roles)
     * @param items List of item build statistics
     * @param runes List of rune configuration statistics
     * @param spells List of summoner spell pair statistics
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
     * Gets the champion identifier.
     *
     * @return The champion ID
     */
    public String getChampionId() { return championId; }

    /**
     * Gets the patch version.
     *
     * @return The patch version string
     */
    public String getPatch() { return patch; }

    /**
     * Gets the queue type identifier.
     *
     * @return The queue ID
     */
    public int getQueueId() { return queueId; }

    /**
     * Gets the role filter that was applied.
     *
     * @return The role name, or null if showing all roles
     */
    public String getRole() { return role; }

    /**
     * Gets the list of item build statistics.
     *
     * @return List of item stats ordered by usage count
     */
    public List<ItemStatDto> getItems() { return items; }

    /**
     * Gets the list of rune configuration statistics.
     *
     * @return List of rune stats ordered by usage count
     */
    public List<RuneStatDto> getRunes() { return runes; }

    /**
     * Gets the list of summoner spell pair statistics.
     *
     * @return List of spell pair stats ordered by usage count
     */
    public List<SpellPairStatDto> getSpells() { return spells; }
}
