// Package declaration: Defines that this class belongs to the dto (Data Transfer Object) package
package com.zerox80.riotapi.dto;


/**
 * Data Transfer Object for champion item build statistics.
 * Represents usage and performance data for a specific item on a champion.
 *
 * This DTO is included in ChampionBuildDto responses and provides information about
 * how often an item is built and its win rate. It includes both statistical data
 * and display information (name, image URL) for the frontend.
 */
public class ItemStatDto {

    // The unique identifier for the item (e.g., 3078 for Trinity Force)
    private int itemId;


    // The number of games this item was built in the dataset (usage count)
    private int count;


    // The number of games won when this item was built (for calculating win rate)
    private int wins;


    // The display name of the item (e.g., "Trinity Force")
    private String name;


    // The URL to the item's image icon for display in the UI
    private String imageUrl;

    /**
     * Constructs an item statistic DTO with usage data and display information.
     *
     * @param itemId The item identifier
     * @param count Total number of games with this item
     * @param wins Number of games won with this item
     * @param name The item's display name
     * @param imageUrl URL to the item's icon image
     */
    public ItemStatDto(int itemId, int count, int wins, String name, String imageUrl) {
        this.itemId = itemId;
        this.count = count;
        this.wins = wins;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the item identifier.
     *
     * @return The item ID
     */
    public int getItemId() { return itemId; }

    /**
     * Gets the usage count (total games with this item).
     *
     * @return The number of games
     */
    public int getCount() { return count; }

    /**
     * Gets the win count (games won with this item).
     *
     * @return The number of wins
     */
    public int getWins() { return wins; }

    /**
     * Gets the item's display name.
     *
     * @return The item name
     */
    public String getName() { return name; }

    /**
     * Gets the URL to the item's icon image.
     *
     * @return The image URL
     */
    public String getImageUrl() { return imageUrl; }
}
