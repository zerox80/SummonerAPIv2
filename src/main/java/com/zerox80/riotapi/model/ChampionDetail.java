package com.zerox80.riotapi.model;

import java.util.List;


public class ChampionDetail {
    
    private final String id;        // e.g., "Anivia"
    
    
    private final String name;      // e.g., "Anivia"
    
    
    private final String title;     // e.g., "the Cryophoenix"
    
    
    private final String lore;      // Long text
    
    
    private final List<String> tags; // Roles
    
    
    private final String imageFull; // e.g., "Anivia.png"
    
    
    private final PassiveSummary passive;
    
    
    private final List<SpellSummary> spells; // Q,W,E,R order from DDragon

    
    public ChampionDetail(String id, String name, String title, String lore,
                          List<String> tags, String imageFull, PassiveSummary passive,
                          List<SpellSummary> spells) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.lore = lore;
        this.tags = tags;
        this.imageFull = imageFull;
        this.passive = passive;
        this.spells = spells;
    }

    
    public String getId() { return id; }
    
    
    public String getName() { return name; }
    
    
    public String getTitle() { return title; }
    
    
    public String getLore() { return lore; }
    
    
    public List<String> getTags() { return tags; }
    
    
    public String getImageFull() { return imageFull; }
    
    
    public PassiveSummary getPassive() { return passive; }
    
    
    public List<SpellSummary> getSpells() { return spells; }
}
