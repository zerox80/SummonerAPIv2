package com.zerox80.riotapi.model;

import jakarta.persistence.*;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "champion_id", nullable = false, length = 64)
    private String championId; // DDragon id or common name key

    @Column(name = "role", nullable = false, length = 16)
    private String role; // TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY, or UNKNOWN

    @Column(name = "patch", nullable = false, length = 16)
    private String patch; // e.g. 15.18

    @Column(name = "queue_id", nullable = false)
    private int queueId; // 420/440

    @Column(name = "item_id", nullable = false)
    private int itemId;

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
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }
}
