package com.zerox80.riotapi.model;

import jakarta.persistence.*;


@Entity
@Table(name = "champion_spell_pair_stats",
       uniqueConstraints = @UniqueConstraint(name = "uk_spellkey", columnNames = {"champion_id","role","patch","queue_id","spell1_id","spell2_id"}),
       indexes = {
           @Index(name = "idx_spell_champ_patch", columnList = "champion_id,patch"),
           @Index(name = "idx_spell_champ_patch_queue", columnList = "champion_id,patch,queue_id"),
           @Index(name = "idx_spell_champ_role_patch_queue", columnList = "champion_id,role,patch,queue_id")
       })
public class ChampionSpellPairStat {
    
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

    
    @Column(name = "spell1_id", nullable = false)
    private int spell1Id;

    
    @Column(name = "spell2_id", nullable = false)
    private int spell2Id;

    
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

    
    public int getSpell1Id() { return spell1Id; }

    
    public void setSpell1Id(int spell1Id) { this.spell1Id = spell1Id; }

    
    public int getSpell2Id() { return spell2Id; }

    
    public void setSpell2Id(int spell2Id) { this.spell2Id = spell2Id; }

    
    public int getCount() { return count; }

    
    public void setCount(int count) { this.count = count; }

    
    public int getWins() { return wins; }

    
    public void setWins(int wins) { this.wins = wins; }
}
