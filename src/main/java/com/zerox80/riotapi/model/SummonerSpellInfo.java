package com.zerox80.riotapi.model;


public class SummonerSpellInfo {
    
    private final int id;
    
    private final String name;
    
    private final String imageFull;

    
    public SummonerSpellInfo(int id, String name, String imageFull) {
        this.id = id;
        this.name = name;
        this.imageFull = imageFull;
    }

    
    public int getId() { return id; }

    
    public String getName() { return name; }

    
    public String getImageFull() { return imageFull; }
}
