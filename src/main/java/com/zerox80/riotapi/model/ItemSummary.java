package com.zerox80.riotapi.model;

public class ItemSummary {
    private final int id; // numeric item id
    private final String name;
    private final String imageFull; // e.g., "1055.png"

    public ItemSummary(int id, String name, String imageFull) {
        this.id = id;
        this.name = name;
        this.imageFull = imageFull;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getImageFull() { return imageFull; }
}
