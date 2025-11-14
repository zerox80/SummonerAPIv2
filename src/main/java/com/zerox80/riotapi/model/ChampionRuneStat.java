// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for JPA Entity annotation to mark this as a database entity
import jakarta.persistence.*;


/**
 * JPA Entity representing champion rune configuration statistics.
 * Stores aggregated data about rune usage for champions in ranked matches.
 *
 * This entity tracks which rune combinations (primary tree, secondary tree, and keystone)
 * are used on each champion, broken down by role, patch version, and queue type.
 * Statistics include usage count and win count to calculate win rates for rune setups.
 * Data is aggregated from high-ELO matches to provide optimal rune recommendations.
 *
 * The table includes unique constraints to prevent duplicate entries and indexes
 * to optimize queries for retrieving top rune configurations.
 */
@Entity
@Table(name = "champion_rune_stats",
       uniqueConstraints = @UniqueConstraint(name = "uk_runekey", columnNames = {"champion_id","role","patch","queue_id","primary_style","sub_style","keystone"}),
       indexes = {
           @Index(name = "idx_rune_champ_patch", columnList = "champion_id,patch"),
           @Index(name = "idx_rune_champ_patch_queue", columnList = "champion_id,patch,queue_id"),
           @Index(name = "idx_rune_champ_role_patch_queue", columnList = "champion_id,role,patch,queue_id")
       })
public class ChampionRuneStat {

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

    // The primary rune tree ID (e.g., 8000 for Precision, 8100 for Domination)
    @Column(name = "primary_style", nullable = false)
    private int primaryStyle;

    // The secondary rune tree ID (different from primary)
    @Column(name = "sub_style", nullable = false)
    private int subStyle;

    // The keystone rune ID (e.g., 8010 for Conqueror, 8005 for Press the Attack)
    @Column(name = "keystone", nullable = false)
    private int keystone;

    // The total number of games where this rune configuration was used (usage count)
    @Column(name = "cnt", nullable = false)
    private int count;

    // The number of games won with this rune configuration (for win rate calculation)
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
     * Gets the primary rune tree ID.
     *
     * @return The primary style ID
     */
    public int getPrimaryStyle() { return primaryStyle; }

    /**
     * Sets the primary rune tree ID.
     *
     * @param primaryStyle The primary style ID to set
     */
    public void setPrimaryStyle(int primaryStyle) { this.primaryStyle = primaryStyle; }

    /**
     * Gets the secondary rune tree ID.
     *
     * @return The secondary style ID
     */
    public int getSubStyle() { return subStyle; }

    /**
     * Sets the secondary rune tree ID.
     *
     * @param subStyle The secondary style ID to set
     */
    public void setSubStyle(int subStyle) { this.subStyle = subStyle; }

    /**
     * Gets the keystone rune ID.
     *
     * @return The keystone ID
     */
    public int getKeystone() { return keystone; }

    /**
     * Sets the keystone rune ID.
     *
     * @param keystone The keystone ID to set
     */
    public void setKeystone(int keystone) { this.keystone = keystone; }

    /**
     * Gets the usage count.
     *
     * @return The number of games with this rune configuration
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
     * @return The number of wins with this rune configuration
     */
    public int getWins() { return wins; }

    /**
     * Sets the win count.
     *
     * @param wins The win count to set
     */
    public void setWins(int wins) { this.wins = wins; }
}
