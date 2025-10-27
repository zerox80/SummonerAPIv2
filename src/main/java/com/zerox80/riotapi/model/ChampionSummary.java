package com.zerox80.riotapi.model;

import java.util.List;

/**
 * Summary information about a League of Legends champion.
 * 
 * <p>This class encapsulates basic champion data retrieved from Data Dragon,
 * including essential information like name, title, roles, and image references.
 * This is used for champion lists, search results, and navigation where detailed
 * champion information is not required.</p>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class ChampionSummary {
    /**
     * The unique identifier for the champion.
     * 
     * <p>This is the champion's key used by Data Dragon and Riot APIs
     * (e.g., "Anivia", "Yasuo", "Jinx"). This identifier is consistent
     * across all regions and game versions, and is used to retrieve
     * detailed champion information.</p>
     */
    private final String id; // DDragon champion id e.g., "Anivia"
    
    /**
     * The display name of the champion.
     * 
     * <p>This is the localized name shown to players in the game client
     * (e.g., "Anivia", "Yasuo", "Jinx"). The name may vary by locale
     * and is used for display purposes in champion lists and search.</p>
     */
    private final String name;
    
    /**
     * The title or epithet of the champion.
     * 
     * <p>This is a short descriptive title that follows the champion's name
     * (e.g., "the Cryophoenix", "the Unforgiven", "the Loose Cannon").
     * The title is localized and provides flavor text about the champion.</p>
     */
    private final String title;
    
    /**
     * List of role tags associated with the champion.
     * 
     * <p>These tags indicate the primary roles the champion is designed to play
     * (e.g., "Mage", "Assassin", "Tank", "Support"). Champions can have multiple
     * tags to reflect their versatility in different positions and team compositions.</p>
     */
    private final List<String> tags;
    
    /**
     * The filename for the champion's full portrait image.
     * 
     * <p>This is the image filename used to construct URLs for champion portraits
     * from Data Dragon (e.g., "Anivia.png"). The full URL can be constructed
     * by combining the Data Dragon base URL with this filename for display in UI components.</p>
     */
    private final String imageFull; // e.g., "Anivia.png"

    /**
     * Constructs a new ChampionSummary with the specified champion information.
     * 
     * @param id The unique champion identifier
     * @param name The localized champion name
     * @param title The localized champion title
     * @param tags List of role tags for the champion
     * @param imageFull The filename for the champion's portrait image
     */
    public ChampionSummary(String id, String name, String title, List<String> tags, String imageFull) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.tags = tags;
        this.imageFull = imageFull;
    }

    /**
     * Gets the unique identifier for the champion.
     * 
     * @return The champion ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the display name of the champion.
     * 
     * @return The champion name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the title or epithet of the champion.
     * 
     * @return The champion title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the list of role tags associated with the champion.
     * 
     * @return List of role tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Gets the filename for the champion's full portrait image.
     * 
     * @return The image filename
     */
    public String getImageFull() {
        return imageFull;
    }
}
