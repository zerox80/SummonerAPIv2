package com.zerox80.riotapi.model;

import java.util.List;


public class SpellSummary {
    
    private final String id;
    
    private final String name;
    
    private String tooltip;
    
    private final String imageFull;

    // Optional values (strings to allow simple rendering without strict parsing)
    
    private String cooldown;   // e.g., "12/11/10/9/8 s"
    
    private String cost;       // e.g., "80/90/100/110/120 Mana"
    
    private String range;      // e.g., "1100"
    
    private String damage;     // e.g., "80/120/160/200/240 (+45% AP)"
    
    private String scaling;    // e.g., "Skaliert mit Fähigkeitsstärke"
    
    private List<String> notes; // bullet points

    
    public SpellSummary(String id, String name, String tooltip, String imageFull) {
        this.id = id;
        this.name = name;
        this.tooltip = tooltip;
        this.imageFull = imageFull;
    }

    
    public String getId() { return id; }

    
    public String getName() { return name; }

    
    public String getTooltip() { return tooltip; }

    
    public void setTooltip(String tooltip) { this.tooltip = tooltip; }

    
    public String getImageFull() { return imageFull; }

    
    public String getCooldown() { return cooldown; }

    
    public void setCooldown(String cooldown) { this.cooldown = cooldown; }

    
    public String getCost() { return cost; }

    
    public void setCost(String cost) { this.cost = cost; }

    
    public String getRange() { return range; }

    
    public void setRange(String range) { this.range = range; }

    
    public String getDamage() { return damage; }

    
    public void setDamage(String damage) { this.damage = damage; }

    
    public String getScaling() { return scaling; }

    
    public void setScaling(String scaling) { this.scaling = scaling; }

    
    public List<String> getNotes() { return notes; }

    
    public void setNotes(List<String> notes) { this.notes = notes; }
}
