// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;


/**
 * Data model representing summary information about a League of Legends item.
 * Contains basic item metadata for display purposes.
 *
 * This model provides essential item information including the item ID, name,
 * and image filename. Used for item displays in builds, inventories, and
 * match history where full item details are not needed.
 */
public class ItemSummary {

    // The numeric item identifier (e.g., 1055 for Doran's Blade)
    private final int id; // numeric item id

    // The item's display name (e.g., "Doran's Blade")
    private final String name;

    // Filename of the item's icon image (e.g., "1055.png")
    private final String imageFull; // e.g., "1055.png"

    /**
     * Constructs an item summary with basic information.
     *
     * @param id The item's unique identifier
     * @param name The item's display name
     * @param imageFull The image filename
     */
    public ItemSummary(int id, String name, String imageFull) {
        this.id = id;
        this.name = name;
        this.imageFull = imageFull;
    }

    /**
     * Gets the item ID.
     *
     * @return The item ID
     */
    public int getId() { return id; }

    /**
     * Gets the item name.
     *
     * @return The item name
     */
    public String getName() { return name; }

    /**
     * Gets the image filename.
     *
     * @return The image filename
     */
    public String getImageFull() { return imageFull; }
}
