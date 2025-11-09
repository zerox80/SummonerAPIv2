package com.zerox80.riotapi.model;

import jakarta.persistence.*;


@Entity
@Table(name = "champion_rune_stats",
       uniqueConstraints = @UniqueConstraint(name = "uk_runekey", columnNames = {"champion_id","role","patch","queue_id","primary_style","sub_style","keystone"}),
       indexes = {
           @Index(name = "idx_rune_champ_patch", columnList = "champion_id,patch"),
           @Index(name = "idx_rune_champ_patch_queue", columnList = "champion_id,patch,queue_id"),
           @Index(name = "idx_rune_champ_role_patch_queue", columnList = "champion_id,role,patch,queue_id")
       })
public class ChampionRuneStat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "champion_id", nullable = false, length = 64)
    private String championId;

    
    @Column(name = "role", nullable = false, length = 16)
    private String role;

    
    @Column(name = "patch", nullable = false, length = 16)
    private String patch;

    
    @Column(name = "queue_id", nullable = false)
    private int queueId;

    
    @Column(name = "primary_style", nullable = false)
    private int primaryStyle;

    
    @Column(name = "sub_style", nullable = false)
    private int subStyle;

    
    @Column(name = "keystone", nullable = false)
    private int keystone;

    
    @Column(name = "cnt", nullable = false)
    private int count;

    
    @Column(name = "wins", nullable = false)
    private int wins;

    
    public Long getId() { return id; }

    
    public String getChampionId() { return championId; }

    
    public void setChampionId(String championId) { this.championId = championId; }

    
    public String getRole() { return role; }

    
    public void setRole(String role) { this.role = role; }

    
    public String getPatch() { return patch; }

    
    public void setPatch(String patch) { this.patch = patch; }

    
    public int getQueueId() { return queueId; }

    
    public void setQueueId(int queueId) { this.queueId = queueId; }

    
    public int getPrimaryStyle() { return primaryStyle; }

    
    public void setPrimaryStyle(int primaryStyle) { this.primaryStyle = primaryStyle; }

    
    public int getSubStyle() { return subStyle; }

    
    public void setSubStyle(int subStyle) { this.subStyle = subStyle; }

    
    public int getKeystone() { return keystone; }

    
    public void setKeystone(int keystone) { this.keystone = keystone; }

    
    public int getCount() { return count; }

    
    public void setCount(int count) { this.count = count; }

    
    public int getWins() { return wins; }

    
    public void setWins(int wins) { this.wins = wins; }
}
