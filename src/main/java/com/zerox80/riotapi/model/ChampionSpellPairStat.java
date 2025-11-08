package com.zerox80.riotapi.model;

import jakarta.persistence.*;

/**
 * JPA entity representing aggregated summoner spell pair statistics for a champion.
 *
 * <p>This entity stores performance metrics for specific combinations of summoner spells
 * when used with a particular champion in a given context (patch, queue, role). The data
 * is used to generate optimal summoner spell recommendations based on aggregated match data.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
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
     * The ID of the first summoner spell in the pair.
     */
    @Column(name = "spell1_id", nullable = false)
    private int spell1Id;

    /**
     * The ID of the second summoner spell in the pair.
     */
    @Column(name = "spell2_id", nullable = false)
    private int spell2Id;

    /**
     * The number of games this spell pair was used.
     */
    @Column(name = "cnt", nullable = false)
    private int count;

    /**
     * The number of games won with this spell pair.
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
     * Gets the ID of the first summoner spell in the pair.
     *
     * @return The first spell ID
     */
    public int getSpell1Id() { return spell1Id; }

    /**
     * Sets the ID of the first summoner spell in the pair.
     *
     * @param spell1Id The first spell ID
     */
    public void setSpell1Id(int spell1Id) { this.spell1Id = spell1Id; }

    /**
     * Gets the ID of the second summoner spell in the pair.
     *
     * @return The second spell ID
     */
    public int getSpell2Id() { return spell2Id; }

    /**
     * Sets the ID of the second summoner spell in the pair.
     *
     * @param spell2Id The second spell ID
     */
    public void setSpell2Id(int spell2Id) { this.spell2Id = spell2Id; }

    /**
     * Gets the number of games this spell pair was used.
     *
     * @return The usage count
     */
    public int getCount() { return count; }

    /**
     * Sets the number of games this spell pair was used.
     *
     * @param count The usage count
     */
    public void setCount(int count) { this.count = count; }

    /**
     * Gets the number of games won with this spell pair.
     *
     * @return The win count
     */
    public int getWins() { return wins; }

    /**
     * Sets the number of games won with this spell pair.
     *
     * @param wins The win count
     */
    public void setWins(int wins) { this.wins = wins; }
}
