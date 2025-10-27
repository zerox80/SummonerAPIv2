package com.zerox80.riotapi.dto;

/**
 * Data Transfer Object representing item usage statistics for a champion.
 * 
 * <p>This class encapsulates performance metrics for a specific item when used
 * with a particular champion, including pick count, win count, and display information.
 * This data is used to generate build recommendations based on aggregated match statistics.</p>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class ItemStatDto {
    /**
     * The unique identifier for the item.
     * 
     * <p>This is the item ID used by Riot Games APIs and Data Dragon
     * to identify items consistently across all regions and game versions.</p>
     */
    private int itemId;
    
    /**
     * The number of times this item was purchased in games analyzed.
     * 
     * <p>This count represents the total number of games where this item
     * was built by players using the specified champion in the given context.</p>
     */
    private int count;
    
    /**
     * The number of games won when this item was purchased.
     * 
     * <p>This represents the number of victories in games where players
     * built this item, used to calculate win rate statistics.</p>
     */
    private int wins;
    
    /**
     * The localized name of the item.
     * 
     * <p>This is the display name shown to players in the game client.
     * The name is localized and may vary by region.</p>
     */
    private String name;
    
    /**
     * The URL for the item's icon image.
     * 
     * <p>This URL points to the item's icon image from Data Dragon,
     * used for visual display in build recommendations and UI components.</p>
     */
    private String imageUrl;

    /**
     * Constructs a new ItemStatDto with the specified item statistics.
     * 
     * @param itemId The unique item identifier
     * @param count The number of times the item was purchased
     * @param wins The number of games won with this item
     * @param name The localized item name
     * @param imageUrl The URL for the item's icon image
     */
    public ItemStatDto(int itemId, int count, int wins, String name, String imageUrl) {
        this.itemId = itemId;
        this.count = count;
        this.wins = wins;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the unique identifier for the item.
     * 
     * @return The item ID
     */
    public int getItemId() { return itemId; }
    
    /**
     * Gets the number of times this item was purchased.
     * 
     * @return The purchase count
     */
    public int getCount() { return count; }
    
    /**
     * Gets the number of games won when this item was purchased.
     * 
     * @return The win count
     */
    public int getWins() { return wins; }
    
    /**
     * Gets the localized name of the item.
     * 
     * @return The item name
     */
    public String getName() { return name; }
    
    /**
     * Gets the URL for the item's icon image.
     * 
     * @return The image URL
     */
    public String getImageUrl() { return imageUrl; }
}
