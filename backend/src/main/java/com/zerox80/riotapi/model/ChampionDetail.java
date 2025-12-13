// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Java List collection to hold multiple tags and spells
import java.util.List;


/**
 * Data model representing detailed information about a League of Legends champion.
 * Contains comprehensive champion data including lore, abilities, and metadata.
 *
 * This model is populated from Data Dragon (DDragon) champion data and provides
 * all information needed to display a champion detail page, including the champion's
 * backstory, passive ability, and all active abilities (Q, W, E, R).
 */
public class ChampionDetail {

    // The champion's unique identifier key (e.g., "Anivia" for Anivia)
    private final String id;        // e.g., "Anivia"

    // The Data Dragon version (e.g., "14.19.1") used for image URLs
    private final String version;

    // The champion's display name (e.g., "Anivia")
    private final String name;      // e.g., "Anivia"


    // The champion's title/subtitle (e.g., "the Cryophoenix")
    private final String title;     // e.g., "the Cryophoenix"


    // The champion's background story/lore (long text describing their history and character)
    private final String lore;      // Long text


    // List of champion role tags (e.g., ["Mage", "Support"])
    private final List<String> tags; // Roles


    // Filename of the champion's splash art image (e.g., "Anivia.png")
    private final String imageFull; // e.g., "Anivia.png"


    // Details about the champion's passive ability
    private final PassiveSummary passive;


    // List of the champion's active abilities in Q, W, E, R order from DDragon
    private final List<SpellSummary> spells; // Q,W,E,R order from DDragon

    /**
     * Constructs a complete champion detail model with all information.
     *
     * @param id The champion's unique identifier
     * @param version The Data Dragon version
     * @param name The champion's display name
     * @param title The champion's title
     * @param lore The champion's background story
     * @param tags List of role tags
     * @param imageFull The image filename
     * @param passive The passive ability information
     * @param spells List of active abilities (Q, W, E, R)
     */
    public ChampionDetail(String id, String version, String name, String title, String lore,
                          List<String> tags, String imageFull, PassiveSummary passive,
                          List<SpellSummary> spells) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.title = title;
        this.lore = lore;
        this.tags = tags;
        this.imageFull = imageFull;
        this.passive = passive;
        this.spells = spells;
    }

    /**
     * Gets the champion's unique identifier.
     *
     * @return The champion ID
     */
    public String getId() { return id; }

    /**
     * Gets the Data Dragon version.
     *
     * @return The version string
     */
    public String getVersion() { return version; }

    /**
     * Gets the champion's display name.
     *
     * @return The champion name
     */
    public String getName() { return name; }

    /**
     * Gets the champion's title.
     *
     * @return The champion title
     */
    public String getTitle() { return title; }

    /**
     * Gets the champion's lore/backstory.
     *
     * @return The champion lore
     */
    public String getLore() { return lore; }

    /**
     * Gets the list of role tags.
     *
     * @return List of role tags
     */
    public List<String> getTags() { return tags; }

    /**
     * Gets the image filename.
     *
     * @return The image filename
     */
    public String getImageFull() { return imageFull; }

    /**
     * Gets the passive ability information.
     *
     * @return The passive ability details
     */
    public PassiveSummary getPassive() { return passive; }

    /**
     * Gets the list of active abilities.
     *
     * @return List of spells (Q, W, E, R)
     */
    public List<SpellSummary> getSpells() { return spells; }
}
