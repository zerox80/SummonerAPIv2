package com.zerox80.riotapi.model;

public class SpellSummary {
    private final String id;
    private final String name;
    private String tooltip;
    private final String imageFull;

    public SpellSummary(String id, String name, String tooltip, String imageFull) {
        this.id = id;
        this.name = name;
        this.tooltip = tooltip;
        this.imageFull = imageFull;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getImageFull() {
        return imageFull;
    }
}
