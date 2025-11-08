package com.zerox80.riotapi.model;

import jakarta.persistence.*;

/**
 * JPA entity representing aggregated item usage statistics for a champion.
 *
 * <p>This entity stores performance metrics for specific items when used with a
 * particular champion in a given context (patch, queue, role). The data is used
 * to generate build recommendations based on aggregated match statistics.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Entity
@Table(name = "champion_item_stats",
       uniqueConstraints = @UniqueConstraint(name = "uk_itemkey", columnNames = {"champion_id","role","patch","queue_id","item_id"}),
       indexes = {
           @Index(name = "idx_item_champ_patch", columnList = "champion_id,patch"),
           @Index(name = "idx_item_champ_patch_queue", columnList = "champion_id,patch,queue_id"),
           @Index(name = "idx_item_champ_role_patch", columnList = "champion_id,role,patch"),
           @Index(name = "idx_item_champ_role_patch_queue", columnList = "champion_id,role,patch,queue_id")
       })
public class ChampionItemStat {
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
    private String championId; // DDragon id or common name key

    /**
     * The role for which these statistics are relevant (e.g., TOP, JUNGLE).
     */
    @Column(name = "role", nullable = false, length = 16)
    private String role; // TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY, or UNKNOWN

    /**
     * The game patch version for which these statistics are relevant.
     */
    @Column(name = "patch", nullable = false, length = 16)
    private String patch; // e.g. 15.18

    /**
     * The queue ID representing the game mode.
     */
    @Column(name = "queue_id", nullable = false)
    private int queueId; // 420/440

    /**
     * The unique identifier for the item.
     */
    @Column(name = "item_id", nullable = false)
    private int itemId;

    /**
     * The number of times this item was purchased.
     */
    @Column(name = "cnt", nullable = false)
    private int count;

    /**
     * The number of games won when this item was purchased.
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
     * Gets the unique identifier for the item.
     *
     * @return The item ID
     */
    public int getItemId() { return itemId; }

    /**
     * Sets the unique identifier for the item.
     *
     * @param itemId The item ID
     */
    public void setItemId(int itemId) { this.itemId = itemId; }

    /**
     * Gets the number of times this item was purchased.
     *
     * @return The purchase count
     */
    public int getCount() { return count; }

    /**
     * Sets the number of times this item was purchased.
     *
     * @param count The purchase count
     */
    public void setCount(int count) { this.count = count; }

    /**
     * Gets the number of games won when this item was purchased.
     *
     * @return The win count
     */
    public int getWins() { return wins; }

    /**
     * Sets the number of games won when this item was purchased.
     *
     * @param wins The win count
     */
    public void setWins(int wins) { this.wins = wins; }
}
