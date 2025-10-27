package com.zerox80.riotapi.model;

import java.util.List;

/**
 * Detailed information about a League of Legends champion.
 * 
 * <p>This class encapsulates comprehensive champion data retrieved from Data Dragon,
 * including basic information like name and title, lore, abilities, and role classifications.
 * This is used to display detailed champion information in the champion browser and build pages.</p>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class ChampionDetail {
    /**
     * The unique identifier for the champion.
     * 
     * <p>This is the champion's key used by Data Dragon and Riot APIs
     * (e.g., "Anivia", "Yasuo", "Jinx"). This identifier is consistent
     * across all regions and game versions.</p>
     */
    private final String id;        // e.g., "Anivia"
    
    /**
     * The display name of the champion.
     * 
     * <p>This is the localized name shown to players in the client
     * (e.g., "Anivia", "Yasuo", "Jinx"). The name may vary by locale.</p>
     */
    private final String name;      // e.g., "Anivia"
    
    /**
     * The title or epithet of the champion.
     * 
     * <p>This is a short descriptive title that follows the champion's name
     * (e.g., "the Cryophoenix", "the Unforgiven", "the Loose Cannon").
     * The title is localized and may vary by region.</p>
     */
    private final String title;     // e.g., "the Cryophoenix"
    
    /**
     * The lore text describing the champion's backstory.
     * 
     * <p>This contains the full narrative description of the champion's
     * history, personality, and place in the League of Legends universe.
     * The lore is localized and can be quite lengthy.</p>
     */
    private final String lore;      // Long text
    
    /**
     * List of role tags associated with the champion.
     * 
     * <p>These tags indicate the primary roles the champion is designed to play
     * (e.g., "Mage", "Assassin", "Tank", "Support"). Champions can have multiple
     * tags to reflect their versatility in different positions.</p>
     */
    private final List<String> tags; // Roles
    
    /**
     * The filename for the champion's full portrait image.
     * 
     * <p>This is the image filename used to construct URLs for champion portraits
     * from Data Dragon (e.g., "Anivia.png"). The full URL can be constructed
     * by combining the Data Dragon base URL with this filename.</p>
     */
    private final String imageFull; // e.g., "Anivia.png"
    
    /**
     * Information about the champion's passive ability.
     * 
     * <p>This contains details about the champion's passive ability,
     * including its name, description, and image information.</p>
     */
    private final PassiveSummary passive;
    
    /**
     * List of the champion's active abilities (spells).
     * 
     * <p>This contains information about all four active abilities (Q, W, E, R)
     * in the order they appear in Data Dragon. Each spell includes details
     * about its name, description, cooldown, cost, and other mechanics.</p>
     */
    private final List<SpellSummary> spells; // Q,W,E,R order from DDragon

    /**
     * Constructs a new ChampionDetail with the specified champion information.
     * 
     * @param id The unique champion identifier
     * @param name The localized champion name
     * @param title The localized champion title
     * @param lore The localized champion lore text
     * @param tags List of role tags for the champion
     * @param imageFull The filename for the champion's portrait image
     * @param passive Information about the champion's passive ability
     * @param spells List of the champion's active abilities
     */
    public ChampionDetail(String id, String name, String title, String lore,
                          List<String> tags, String imageFull, PassiveSummary passive,
                          List<SpellSummary> spells) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.lore = lore;
        this.tags = tags;
        this.imageFull = imageFull;
        this.passive = passive;
        this.spells = spells;
    }

    /**
     * Gets the unique identifier for the champion.
     * 
     * @return The champion ID
     */
    public String getId() { return id; }
    
    /**
     * Gets the display name of the champion.
     * 
     * @return The champion name
     */
    public String getName() { return name; }
    
    /**
     * Gets the title or epithet of the champion.
     * 
     * @return The champion title
     */
    public String getTitle() { return title; }
    
    /**
     * Gets the lore text describing the champion's backstory.
     * 
     * @return The champion lore
     */
    public String getLore() { return lore; }
    
    /**
     * Gets the list of role tags associated with the champion.
     * 
     * @return List of role tags
     */
    public List<String> getTags() { return tags; }
    
    /**
     * Gets the filename for the champion's full portrait image.
     * 
     * @return The image filename
     */
    public String getImageFull() { return imageFull; }
    
    /**
     * Gets information about the champion's passive ability.
     * 
     * @return The passive ability information
     */
    public PassiveSummary getPassive() { return passive; }
    
    /**
     * Gets the list of the champion's active abilities.
     * 
     * @return List of spell information in Q,W,E,R order
     */
    public List<SpellSummary> getSpells() { return spells; }
}
