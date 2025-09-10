package com.zerox80.riotapi.dto;

public class ItemStatDto {
    private int itemId;
    private int count;
    private int wins;
    private String name;
    private String imageUrl;

    public ItemStatDto(int itemId, int count, int wins, String name, String imageUrl) {
        this.itemId = itemId;
        this.count = count;
        this.wins = wins;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public int getItemId() { return itemId; }
    public int getCount() { return count; }
    public int getWins() { return wins; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
}
