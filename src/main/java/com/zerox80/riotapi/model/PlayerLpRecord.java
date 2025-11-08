package com.zerox80.riotapi.model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity representing a player's League Points (LP) at a specific point in time.
 *
 * <p>This entity is used to track a player's LP history over time, allowing for
 * analysis of their ranked progression. Each record stores the player's PUUID,
 * the queue type, a timestamp, and their LP at that time.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Entity
@Table(name = "player_lp_records", indexes = {
    @Index(name = "idx_puuid_queuetype_timestamp", columnList = "puuid, queue_type, lp_timestamp")
})
public class PlayerLpRecord {

    /** The unique identifier for the LP record. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The player's PUUID. */
    @Column(nullable = false)
    private String puuid;

    /** The type of ranked queue. */
    @Column(nullable = false)
    private String queueType;

    /** The timestamp of when the LP was recorded. */
    @Column(name = "lp_timestamp", nullable = false)
    private Instant timestamp;

    /** The player's League Points at the time of the record. */
    @Column(nullable = false)
    private int leaguePoints;

    /** The player's tier at the time of the record. */
    private String tier;
    /** The player's rank within the tier at the time of the record. */
    private String rank;

    /**
     * Default constructor.
     */
    public PlayerLpRecord() {
    }

    /**
     * Constructs a new PlayerLpRecord with the specified details.
     *
     * @param puuid The player's PUUID
     * @param queueType The type of ranked queue
     * @param timestamp The timestamp of the record
     * @param leaguePoints The player's LP
     * @param tier The player's tier
     * @param rank The player's rank
     */
    public PlayerLpRecord(String puuid, String queueType, Instant timestamp, int leaguePoints, String tier, String rank) {
        this.puuid = puuid;
        this.queueType = queueType;
        this.timestamp = timestamp;
        this.leaguePoints = leaguePoints;
        this.tier = tier;
        this.rank = rank;
    }

    /**
     * Gets the unique identifier for the LP record.
     *
     * @return The record ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the LP record.
     *
     * @param id The record ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the player's PUUID.
     *
     * @return The PUUID
     */
    public String getPuuid() {
        return puuid;
    }

    /**
     * Sets the player's PUUID.
     *
     * @param puuid The PUUID
     */
    public void setPuuid(String puuid) {
        this.puuid = puuid;
    }

    /**
     * Gets the type of ranked queue.
     *
     * @return The queue type
     */
    public String getQueueType() {
        return queueType;
    }

    /**
     * Sets the type of ranked queue.
     *
     * @param queueType The queue type
     */
    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    /**
     * Gets the timestamp of when the LP was recorded.
     *
     * @return The timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of when the LP was recorded.
     *
     * @param timestamp The timestamp
     */
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the player's League Points.
     *
     * @return The league points
     */
    public int getLeaguePoints() {
        return leaguePoints;
    }

    /**
     * Sets the player's League Points.
     *
     * @param leaguePoints The league points
     */
    public void setLeaguePoints(int leaguePoints) {
        this.leaguePoints = leaguePoints;
    }

    /**
     * Gets the player's tier.
     *
     * @return The tier
     */
    public String getTier() {
        return tier;
    }

    /**
     * Sets the player's tier.
     *
     * @param tier The tier
     */
    public void setTier(String tier) {
        this.tier = tier;
    }

    /**
     * Gets the player's rank within the tier.
     *
     * @return The rank
     */
    public String getRank() {
        return rank;
    }

    /**
     * Sets the player's rank within the tier.
     *
     * @param rank The rank
     */
    public void setRank(String rank) {
        this.rank = rank;
    }
}
