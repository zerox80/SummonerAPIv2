// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for JPA Entity annotation to mark this as a database entity
import jakarta.persistence.*;


/**
 * JPA Entity representing champion summoner spell pair statistics.
 * Stores aggregated data about summoner spell combinations for champions in ranked matches.
 *
 * This entity tracks which summoner spell pairs (e.g., Flash + Ignite, Flash + Teleport)
 * are used on each champion, broken down by role, patch version, and queue type.
 * Statistics include usage count and win count to calculate win rates for spell combinations.
 * Data is aggregated from high-ELO matches to provide optimal summoner spell recommendations.
 *
 * The table includes unique constraints to prevent duplicate entries and indexes
 * to optimize queries for retrieving top summoner spell pairs.
 */
@Entity
@Table(name = "champion_spell_pair_stats",
       uniqueConstraints = @UniqueConstraint(name = "uk_spellkey", columnNames = {"champion_id","role","patch","queue_id","spell1_id","spell2_id"}),
       indexes = {
           @Index(name = "idx_spell_champ_patch", columnList = "champion_id,patch"),
           @Index(name = "idx_spell_champ_patch_queue", columnList = "champion_id,patch,queue_id"),
           @Index(name = "idx_spell_champ_role_patch_queue", columnList = "champion_id,role,patch,queue_id")
       })
public class ChampionSpellPairStat {

    // Primary key - auto-generated unique identifier for each record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The champion identifier (e.g., "266" for Aatrox)
    @Column(name = "champion_id", nullable = false, length = 64)
    private String championId;

    // The lane role (TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY, or UNKNOWN)
    @Column(name = "role", nullable = false, length = 16)
    private String role;

    // The game patch version this statistic applies to (e.g., "15.18")
    @Column(name = "patch", nullable = false, length = 16)
    private String patch;

    // The ranked queue type (420 for Solo/Duo, 440 for Flex)
    @Column(name = "queue_id", nullable = false)
    private int queueId;

    // The first summoner spell ID (e.g., 4 for Flash)
    @Column(name = "spell1_id", nullable = false)
    private int spell1Id;

    // The second summoner spell ID (e.g., 14 for Ignite, 12 for Teleport)
    @Column(name = "spell2_id", nullable = false)
    private int spell2Id;

    // The total number of games where this spell pair was used (usage count)
    @Column(name = "cnt", nullable = false)
    private int count;

    // The number of games won with this spell pair (for win rate calculation)
    @Column(name = "wins", nullable = false)
    private int wins;

    /**
     * Gets the primary key ID.
     *
     * @return The record ID
     */
    public Long getId() { return id; }

    /**
     * Gets the champion identifier.
     *
     * @return The champion ID
     */
    public String getChampionId() { return championId; }

    /**
     * Sets the champion identifier.
     *
     * @param championId The champion ID to set
     */
    public void setChampionId(String championId) { this.championId = championId; }

    /**
     * Gets the lane role.
     *
     * @return The role
     */
    public String getRole() { return role; }

    /**
     * Sets the lane role.
     *
     * @param role The role to set
     */
    public void setRole(String role) { this.role = role; }

    /**
     * Gets the patch version.
     *
     * @return The patch version
     */
    public String getPatch() { return patch; }

    /**
     * Sets the patch version.
     *
     * @param patch The patch version to set
     */
    public void setPatch(String patch) { this.patch = patch; }

    /**
     * Gets the queue type ID.
     *
     * @return The queue ID
     */
    public int getQueueId() { return queueId; }

    /**
     * Sets the queue type ID.
     *
     * @param queueId The queue ID to set
     */
    public void setQueueId(int queueId) { this.queueId = queueId; }

    /**
     * Gets the first summoner spell ID.
     *
     * @return The first spell ID
     */
    public int getSpell1Id() { return spell1Id; }

    /**
     * Sets the first summoner spell ID.
     *
     * @param spell1Id The first spell ID to set
     */
    public void setSpell1Id(int spell1Id) { this.spell1Id = spell1Id; }

    /**
     * Gets the second summoner spell ID.
     *
     * @return The second spell ID
     */
    public int getSpell2Id() { return spell2Id; }

    /**
     * Sets the second summoner spell ID.
     *
     * @param spell2Id The second spell ID to set
     */
    public void setSpell2Id(int spell2Id) { this.spell2Id = spell2Id; }

    /**
     * Gets the usage count.
     *
     * @return The number of games with this spell pair
     */
    public int getCount() { return count; }

    /**
     * Sets the usage count.
     *
     * @param count The usage count to set
     */
    public void setCount(int count) { this.count = count; }

    /**
     * Gets the win count.
     *
     * @return The number of wins with this spell pair
     */
    public int getWins() { return wins; }

    /**
     * Sets the win count.
     *
     * @param wins The win count to set
     */
    public void setWins(int wins) { this.wins = wins; }
}
