package com.zerox80.riotapi.dto;

import java.util.List;


public class ChampionBuildDto {
    
    private String championId;
    
    
    private String patch;
    
    
    private int queueId;
    
    
    private String role; // optional filter used
    
    
    private List<ItemStatDto> items;
    
    
    private List<RuneStatDto> runes;
    
    
    private List<SpellPairStatDto> spells;

    
    public ChampionBuildDto(String championId, String patch, int queueId, String role,
                            List<ItemStatDto> items, List<RuneStatDto> runes, List<SpellPairStatDto> spells) {
        this.championId = championId;
        this.patch = patch;
        this.queueId = queueId;
        this.role = role;
        this.items = items;
        this.runes = runes;
        this.spells = spells;
    }

    
    public String getChampionId() { return championId; }
    
    
    public String getPatch() { return patch; }
    
    
    public int getQueueId() { return queueId; }
    
    
    public String getRole() { return role; }
    
    
    public List<ItemStatDto> getItems() { return items; }
    
    
    public List<RuneStatDto> getRunes() { return runes; }
    
    
    public List<SpellPairStatDto> getSpells() { return spells; }
}
