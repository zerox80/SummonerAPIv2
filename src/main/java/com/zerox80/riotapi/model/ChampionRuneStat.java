package com.zerox80.riotapi.model;

import jakarta.persistence.*;

/**
 * JPA entity representing aggregated rune usage statistics for a champion.
 *
 * <p>This entity stores performance metrics for specific rune configurations when used
 * with a particular champion in a given context (patch, queue, role). The data is used
 * to generate optimal rune recommendations based on aggregated match statistics.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
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
    /**
     * The unique identifier for the statistics record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The champion ID for which these statistics apply.
     */
    @Column(name = "champion_id", nullable = false, length = 64)
    private String championId;

    /**
     * The role for which these statistics are relevant (e.g., TOP, JUNGLE).
     */
    @Column(name = "role", nullable = false, length = 16)
    private String role;

    /**
     * The game patch version for which these statistics are relevant.
     */
    @Column(name = "patch", nullable = false, length = 16)
    private String patch;

    /**
     * The queue ID representing the game mode.
     */
    @Column(name = "queue_id", nullable = false)
    private int queueId;

    /**
     * The ID of the primary rune path.
     */
    @Column(name = "primary_style", nullable = false)
    private int primaryStyle;

    /**
     * The ID of the secondary rune path.
     */
    @Column(name = "sub_style", nullable = false)
    private int subStyle;

    /**
     * The ID of the keystone rune.
     */
    @Column(name = "keystone", nullable = false)
    private int keystone;

    /**
     * The number of games this rune configuration was used.
     */
    @Column(name = "cnt", nullable = false)
    private int count;

    /**
     * The number of games won with this rune configuration.
     */
    @Column(name = "wins", nullable = false)
    private int wins;

    /**
     * Gets the unique identifier for the statistics record.
     *
     * @return The record ID
     */
    public Long getId() { return id; }

    /**
     * Gets the champion ID for these statistics.
     *
     * @return The champion ID
     */
    public String getChampionId() { return championId; }

    /**
     * Sets the champion ID for these statistics.
     *
     * @param championId The champion ID
     */
    public void setChampionId(String championId) { this.championId = championId; }

    /**
     * Gets the role for which these statistics are relevant.
     *
     * @return The role
     */
    public String getRole() { return role; }

    /**
     * Sets the role for which these statistics are relevant.
     *
     * @param role The role
     */
    public void setRole(String role) { this.role = role; }

    /**
     * Gets the game patch version for these statistics.
     *
     * @return The patch version
     */
    public String getPatch() { return patch; }

    /**
     * Sets the game patch version for these statistics.
     *
     * @param patch The patch version
     */
    public void setPatch(String patch) { this.patch = patch; }

    /**
     * Gets the queue ID representing the game mode.
     *
     * @return The queue ID
     */
    public int getQueueId() { return queueId; }

    /**
     * Sets the queue ID representing the game mode.
     *
     * @param queueId The queue ID
     */
    public void setQueueId(int queueId) { this.queueId = queueId; }

    /**
     * Gets the ID of the primary rune path.
     *
     * @return The primary style ID
     */
    public int getPrimaryStyle() { return primaryStyle; }

    /**
     * Sets the ID of the primary rune path.
     *
     * @param primaryStyle The primary style ID
     */
    public void setPrimaryStyle(int primaryStyle) { this.primaryStyle = primaryStyle; }

    /**
     * Gets the ID of the secondary rune path.
     *
     * @return The secondary style ID
     */
    public int getSubStyle() { return subStyle; }

    /**
     * Sets the ID of the secondary rune path.
     *
     * @param subStyle The secondary style ID
     */
    public void setSubStyle(int subStyle) { this.subStyle = subStyle; }

    /**
     * Gets the ID of the keystone rune.
     *
     * @return The keystone ID
     */
    public int getKeystone() { return keystone; }

    /**
     * Sets the ID of the keystone rune.
     *
     * @param keystone The keystone ID
     */
    public void setKeystone(int keystone) { this.keystone = keystone; }

    /**
     * Gets the number of games this rune configuration was used.
     *
     * @return The usage count
     */
    public int getCount() { return count; }

    /**
     * Sets the number of games this rune configuration was used.
     *
     * @param count The usage count
     */
    public void setCount(int count) { this.count = count; }

    /**
     * Gets the number of games won with this rune configuration.
     *
     * @return The win count
     */
    public int getWins() { return wins; }

    /**
     * Sets the number of games won with this rune configuration.
     *
     * @param wins The win count
     */
    public void setWins(int wins) { this.wins = wins; }
}
