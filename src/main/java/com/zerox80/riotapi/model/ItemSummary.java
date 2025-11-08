package com.zerox80.riotapi.model;

/**
 * Summary information about a League of Legends item.
 *
 * <p>This class encapsulates basic item data retrieved from Data Dragon,
 * including the item's ID, name, and image filename. This is used for
 * displaying item information in build recommendations and other UI components.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class ItemSummary {
    /** The unique identifier for the item. */
    private final int id; // numeric item id
    /** The localized name of the item. */
    private final String name;
    /** The filename for the item's image. */
    private final String imageFull; // e.g., "1055.png"

    /**
     * Constructs a new ItemSummary with the specified item information.
     *
     * @param id The unique item identifier
     * @param name The localized item name
     * @param imageFull The filename for the item's image
     */
    public ItemSummary(int id, String name, String imageFull) {
        this.id = id;
        this.name = name;
        this.imageFull = imageFull;
    }

    /**
     * Gets the unique identifier for the item.
     *
     * @return The item ID
     */
    public int getId() { return id; }

    /**
     * Gets the localized name of the item.
     *
     * @return The item name
     */
    public String getName() { return name; }

    /**
     * Gets the filename for the item's image.
     *
     * @return The image filename
     */
    public String getImageFull() { return imageFull; }
}
