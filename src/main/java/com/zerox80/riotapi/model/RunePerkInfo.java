package com.zerox80.riotapi.model;

public class RunePerkInfo {
    private final int id;
    private final String name;
    private final String iconFull; // e.g., "perk-images/Styles/Precision/Conqueror/Conqueror.png"

    public RunePerkInfo(int id, String name, String iconFull) {
        this.id = id;
        this.name = name;
        this.iconFull = iconFull;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getIconFull() { return iconFull; }
}
