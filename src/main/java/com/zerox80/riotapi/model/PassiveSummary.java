// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;


/**
 * Data model representing a champion's passive ability information.
 * Contains descriptive details about a champion's passive ability.
 *
 * This model provides the name, description, and icon for a champion's passive ability.
 * Passive abilities are unique permanent effects that each champion has,
 * distinct from their active Q, W, E, R abilities.
 */
public class PassiveSummary {

    // The passive ability's name (e.g., "Rebirth" for Anivia)
    private final String name;

    // The passive ability's description explaining what it does
    private final String description;

    // Filename of the passive ability's icon image
    private final String imageFull;

    /**
     * Constructs a passive ability summary with all information.
     *
     * @param name The passive ability's name
     * @param description The passive ability's description
     * @param imageFull The image filename
     */
    public PassiveSummary(String name, String description, String imageFull) {
        this.name = name;
        this.description = description;
        this.imageFull = imageFull;
    }

    /**
     * Gets the passive ability name.
     *
     * @return The passive name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the passive ability description.
     *
     * @return The passive description
     */
    public String getDescription() {
        return description;
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
