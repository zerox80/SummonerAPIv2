package com.zerox80.riotapi.dto;


public class RuneStatDto {
    
    private int primaryStyle;
    
    
    private int subStyle;
    
    
    private int keystone;
    
    
    private int count;
    
    
    private int wins;
    
    
    private String primaryStyleName;
    
    
    private String subStyleName;
    
    
    private String keystoneName;
    
    
    private String keystoneIconUrl;

    
    public RuneStatDto(int primaryStyle, int subStyle, int keystone, int count, int wins,
                       String primaryStyleName, String subStyleName, String keystoneName, String keystoneIconUrl) {
        this.primaryStyle = primaryStyle;
        this.subStyle = subStyle;
        this.keystone = keystone;
        this.count = count;
        this.wins = wins;
        this.primaryStyleName = primaryStyleName;
        this.subStyleName = subStyleName;
        this.keystoneName = keystoneName;
        this.keystoneIconUrl = keystoneIconUrl;
    }

    
    public int getPrimaryStyle() { return primaryStyle; }
    
    
    public int getSubStyle() { return subStyle; }
    
    
    public int getKeystone() { return keystone; }
    
    
    public int getCount() { return count; }
    
    
    public int getWins() { return wins; }
    
    
    public String getPrimaryStyleName() { return primaryStyleName; }
    
    
    public String getSubStyleName() { return subStyleName; }
    
    
    public String getKeystoneName() { return keystoneName; }
    
    
    public String getKeystoneIconUrl() { return keystoneIconUrl; }
}
