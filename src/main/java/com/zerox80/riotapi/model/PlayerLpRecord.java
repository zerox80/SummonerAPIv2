package com.zerox80.riotapi.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "player_lp_records", indexes = {
    @Index(name = "idx_puuid_queuetype_timestamp", columnList = "puuid, queue_type, lp_timestamp DESC")
})
public class PlayerLpRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String puuid;

    @Column(nullable = false)
    private String queueType;

    @Column(name = "lp_timestamp", nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private int leaguePoints;

    private String tier;
    private String rank;

    public PlayerLpRecord() {
    }

    public PlayerLpRecord(String puuid, String queueType, Instant timestamp, int leaguePoints, String tier, String rank) {
        this.puuid = puuid;
        this.queueType = queueType;
        this.timestamp = timestamp;
        this.leaguePoints = leaguePoints;
        this.tier = tier;
        this.rank = rank;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPuuid() {
        return puuid;
    }

    public void setPuuid(String puuid) {
        this.puuid = puuid;
    }

    public String getQueueType() {
        return queueType;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getLeaguePoints() {
        return leaguePoints;
    }

    public void setLeaguePoints(int leaguePoints) {
        this.leaguePoints = leaguePoints;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
} 
