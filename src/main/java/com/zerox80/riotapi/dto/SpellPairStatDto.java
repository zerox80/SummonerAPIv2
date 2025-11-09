package com.zerox80.riotapi.dto;


public class SpellPairStatDto {
    
    private int spell1Id;
    
    
    private int spell2Id;
    
    
    private int count;
    
    
    private int wins;
    
    
    private String spell1Name;
    
    
    private String spell2Name;
    
    
    private String spell1IconUrl;
    
    
    private String spell2IconUrl;

    
    public SpellPairStatDto(int spell1Id, int spell2Id, int count, int wins,
                            String spell1Name, String spell2Name,
                            String spell1IconUrl, String spell2IconUrl) {
        this.spell1Id = spell1Id;
        this.spell2Id = spell2Id;
        this.count = count;
        this.wins = wins;
        this.spell1Name = spell1Name;
        this.spell2Name = spell2Name;
        this.spell1IconUrl = spell1IconUrl;
        this.spell2IconUrl = spell2IconUrl;
    }

    
    public int getSpell1Id() { return spell1Id; }
    
    
    public int getSpell2Id() { return spell2Id; }
    
    
    public int getCount() { return count; }
    
    
    public int getWins() { return wins; }
    
    
    public String getSpell1Name() { return spell1Name; }
    
    
    public String getSpell2Name() { return spell2Name; }
    
    
    public String getSpell1IconUrl() { return spell1IconUrl; }
    
    
    public String getSpell2IconUrl() { return spell2IconUrl; }
}
