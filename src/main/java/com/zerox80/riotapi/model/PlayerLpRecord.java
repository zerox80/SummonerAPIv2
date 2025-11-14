// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for JPA Entity annotation to mark this as a database entity
import jakarta.persistence.*;
// Import for Java Instant to handle timestamp storage
import java.time.Instant;


/**
 * JPA Entity representing a historical League Points (LP) record for a player.
 * Stores snapshots of a player's ranked status at specific points in time.
 *
 * This entity tracks the ranked progression of players by recording their LP, tier,
 * and rank at various timestamps. Used to calculate LP gains/losses per game by
 * comparing records before and after matches. The indexed timestamp allows efficient
 * time-based queries to find LP values at match start/end times.
 */
@Entity
@Table(name = "player_lp_records", indexes = {
    @Index(name = "idx_puuid_queuetype_timestamp", columnList = "puuid, queue_type, lp_timestamp")
})
public class PlayerLpRecord {

    // Primary key - auto-generated unique identifier for each record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The player's unique universal identifier (PUUID)
    @Column(nullable = false)
    private String puuid;

    // The ranked queue type (e.g., "RANKED_SOLO_5x5", "RANKED_FLEX_SR")
    @Column(nullable = false)
    private String queueType;

    // The timestamp when this LP snapshot was recorded
    @Column(name = "lp_timestamp", nullable = false)
    private Instant timestamp;

    // The player's League Points at this timestamp
    @Column(nullable = false)
    private int leaguePoints;

    // The player's tier at this timestamp (e.g., "GOLD", "PLATINUM")
    private String tier;

    // The player's rank within the tier (e.g., "I", "II", "III", "IV")
    private String rank;

    /**
     * No-argument constructor required by JPA.
     */
    public PlayerLpRecord() {
    }

    /**
     * Constructs a complete LP record with all information.
     *
     * @param puuid The player's PUUID
     * @param queueType The queue type
     * @param timestamp The record timestamp
     * @param leaguePoints The LP value
     * @param tier The tier
     * @param rank The rank within tier
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
     * Gets the primary key ID.
     *
     * @return The record ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the primary key ID.
     *
     * @param id The record ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the player PUUID.
     *
     * @return The PUUID
     */
    public String getPuuid() {
        return puuid;
    }

    /**
     * Sets the player PUUID.
     *
     * @param puuid The PUUID
     */
    public void setPuuid(String puuid) {
        this.puuid = puuid;
    }

    /**
     * Gets the queue type.
     *
     * @return The queue type
     */
    public String getQueueType() {
        return queueType;
    }

    /**
     * Sets the queue type.
     *
     * @param queueType The queue type
     */
    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    /**
     * Gets the record timestamp.
     *
     * @return The timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the record timestamp.
     *
     * @param timestamp The timestamp
     */
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the League Points value.
     *
     * @return The LP value
     */
    public int getLeaguePoints() {
        return leaguePoints;
    }

    /**
     * Sets the League Points value.
     *
     * @param leaguePoints The LP value
     */
    public void setLeaguePoints(int leaguePoints) {
        this.leaguePoints = leaguePoints;
    }

    /**
     * Gets the tier.
     *
     * @return The tier
     */
    public String getTier() {
        return tier;
    }

    /**
     * Sets the tier.
     *
     * @param tier The tier
     */
    public void setTier(String tier) {
        this.tier = tier;
    }

    /**
     * Gets the rank.
     *
     * @return The rank
     */
    public String getRank() {
        return rank;
    }

    /**
     * Sets the rank.
     *
     * @param rank The rank
     */
    public void setRank(String rank) {
        this.rank = rank;
    }
}
