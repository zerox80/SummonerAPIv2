// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;


/**
 * Data model representing information about a summoner spell.
 * Contains metadata for summoner spells used in League of Legends matches.
 *
 * This model provides the ID, name, and icon for summoner spells like Flash, Ignite,
 * Teleport, etc. Used for displaying summoner spell information in champion builds
 * and match histories. Each player selects two summoner spells before a match.
 */
public class SummonerSpellInfo {

    // The summoner spell unique identifier (e.g., 4 for Flash, 14 for Ignite)
    private final int id;

    // The summoner spell display name (e.g., "Flash")
    private final String name;

    // Filename of the summoner spell's icon image
    private final String imageFull;

    /**
     * Constructs a summoner spell info object with all information.
     *
     * @param id The summoner spell unique identifier
     * @param name The summoner spell display name
     * @param imageFull The image filename
     */
    public SummonerSpellInfo(int id, String name, String imageFull) {
        this.id = id;
        this.name = name;
        this.imageFull = imageFull;
    }

    /**
     * Gets the summoner spell ID.
     *
     * @return The spell ID
     */
    public int getId() { return id; }

    /**
     * Gets the summoner spell name.
     *
     * @return The spell name
     */
    public String getName() { return name; }

    /**
     * Gets the image filename.
     *
     * @return The image filename
     */
    public String getImageFull() { return imageFull; }
}
