package com.zerox80.riotapi.model;

/**
 * Information about a single summoner spell.
 *
 * <p>This class encapsulates basic information about a single summoner spell,
 * including its ID, name, and image path. This is used for displaying summoner
 * spell information in build recommendations and other UI components.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class SummonerSpellInfo {
    /** The unique identifier for the summoner spell. */
    private final int id;
    /** The localized name of the summoner spell. */
    private final String name;
    /** The filename for the summoner spell's image. */
    private final String imageFull;

    /**
     * Constructs a new SummonerSpellInfo with the specified information.
     *
     * @param id The unique summoner spell identifier
     * @param name The localized summoner spell name
     * @param imageFull The filename for the summoner spell's image
     */
    public SummonerSpellInfo(int id, String name, String imageFull) {
        this.id = id;
        this.name = name;
        this.imageFull = imageFull;
    }

    /**
     * Gets the unique identifier for the summoner spell.
     *
     * @return The summoner spell ID
     */
    public int getId() { return id; }

    /**
     * Gets the localized name of the summoner spell.
     *
     * @return The summoner spell name
     */
    public String getName() { return name; }

    /**
     * Gets the filename for the summoner spell's image.
     *
     * @return The image filename
     */
    public String getImageFull() { return imageFull; }
}
