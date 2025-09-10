package com.zerox80.riotapi.model;

import java.util.List;

public class ChampionSummary {
    private final String id; // DDragon champion id e.g., "Anivia"
    private final String name;
    private final String title;
    private final List<String> tags;
    private final String imageFull; // e.g., "Anivia.png"

    public ChampionSummary(String id, String name, String title, List<String> tags, String imageFull) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.tags = tags;
        this.imageFull = imageFull;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getImageFull() {
        return imageFull;
    }
}
