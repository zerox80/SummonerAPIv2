package com.zerox80.riotapi.model;

public class PassiveSummary {
    private final String name;
    private final String description;
    private final String imageFull;

    public PassiveSummary(String name, String description, String imageFull) {
        this.name = name;
        this.description = description;
        this.imageFull = imageFull;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageFull() {
        return imageFull;
    }
}
