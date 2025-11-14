// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Java List collection to hold multiple role tags
import java.util.List;


/**
 * Data model representing summary information about a League of Legends champion.
 * Contains basic champion metadata for list views and champion selection interfaces.
 *
 * This model provides essential champion information without the detailed lore
 * and ability data found in ChampionDetail. Used for champion lists, search results,
 * and other views where full details are not needed.
 */
public class ChampionSummary {

    // The champion's unique identifier key from DDragon (e.g., "Anivia")
    private final String id; // DDragon champion id e.g., "Anivia"


    // The champion's display name (e.g., "Anivia")
    private final String name;


    // The champion's title/subtitle (e.g., "the Cryophoenix")
    private final String title;


    // List of champion role tags (e.g., ["Mage", "Support"])
    private final List<String> tags;


    // Filename of the champion's portrait image (e.g., "Anivia.png")
    private final String imageFull; // e.g., "Anivia.png"

    /**
     * Constructs a champion summary with basic information.
     *
     * @param id The champion's unique identifier
     * @param name The champion's display name
     * @param title The champion's title
     * @param tags List of role tags
     * @param imageFull The image filename
     */
    public ChampionSummary(String id, String name, String title, List<String> tags, String imageFull) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.tags = tags;
        this.imageFull = imageFull;
    }

    /**
     * Gets the champion's unique identifier.
     *
     * @return The champion ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the champion's display name.
     *
     * @return The champion name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the champion's title.
     *
     * @return The champion title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the list of role tags.
     *
     * @return List of role tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Gets the image filename.
     *
     * @return The image filename
     */
    public String getImageFull() {
        return imageFull;
    }
}
