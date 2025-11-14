// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for JPA Entity annotation to mark this as a database entity
import jakarta.persistence.*;


/**
 * JPA Entity representing champion item build statistics.
 * Stores aggregated data about item usage for champions in ranked matches.
 *
 * This entity tracks which items are built on each champion, broken down by role,
 * patch version, and queue type. Statistics include usage count and win count to
 * calculate win rates. Data is aggregated from high-ELO matches to provide optimal
 * item build recommendations.
 *
 * The table includes unique constraints to prevent duplicate entries and multiple
 * indexes to optimize common query patterns for retrieving top item builds.
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

    // Primary key - auto-generated unique identifier for each record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The champion identifier (DDragon ID or common name key, e.g., "266" for Aatrox)
    @Column(name = "champion_id", nullable = false, length = 64)
    private String championId; // DDragon id or common name key

    // The lane role (TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY, or UNKNOWN)
    @Column(name = "role", nullable = false, length = 16)
    private String role; // TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY, or UNKNOWN

    // The game patch version this statistic applies to (e.g., "15.18")
    @Column(name = "patch", nullable = false, length = 16)
    private String patch; // e.g. 15.18

    // The ranked queue type (420 for Solo/Duo, 440 for Flex)
    @Column(name = "queue_id", nullable = false)
    private int queueId; // 420/440

    // The unique identifier for the item being built
    @Column(name = "item_id", nullable = false)
    private int itemId;

    // The total number of games where this item was built (usage count)
    @Column(name = "cnt", nullable = false)
    private int count;

    // The number of games won when this item was built (for win rate calculation)
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
     * Gets the item ID.
     *
     * @return The item ID
     */
    public int getItemId() { return itemId; }

    /**
     * Sets the item ID.
     *
     * @param itemId The item ID to set
     */
    public void setItemId(int itemId) { this.itemId = itemId; }

    /**
     * Gets the usage count.
     *
     * @return The number of games with this item
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
     * @return The number of wins with this item
     */
    public int getWins() { return wins; }

    /**
     * Sets the win count.
     *
     * @param wins The win count to set
     */
    public void setWins(int wins) { this.wins = wins; }
}
