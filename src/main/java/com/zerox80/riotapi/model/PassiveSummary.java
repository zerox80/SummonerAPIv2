package com.zerox80.riotapi.model;

/**
 * Summary information about a champion's passive ability.
 *
 * <p>This class encapsulates basic data about a champion's passive ability,
 * including its name, description, and image filename. This is used for
 * displaying passive ability information in champion detail views.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class PassiveSummary {
    /** The localized name of the passive ability. */
    private final String name;
    /** The localized description of the passive ability. */
    private final String description;
    /** The filename for the passive ability's image. */
    private final String imageFull;

    /**
     * Constructs a new PassiveSummary with the specified information.
     *
     * @param name The localized name of the passive ability
     * @param description The localized description of the passive ability
     * @param imageFull The filename for the passive ability's image
     */
    public PassiveSummary(String name, String description, String imageFull) {
        this.name = name;
        this.description = description;
        this.imageFull = imageFull;
    }

    /**
     * Gets the localized name of the passive ability.
     *
     * @return The passive ability name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the localized description of the passive ability.
     *
     * @return The passive ability description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the filename for the passive ability's image.
     *
     * @return The image filename
     */
    public String getImageFull() {
        return imageFull;
    }
}
