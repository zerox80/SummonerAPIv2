// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;


/**
 * Data model representing information about a specific rune or perk.
 * Contains metadata for individual runes within the rune system.
 *
 * This model provides the ID, name, and icon path for a specific rune or perk.
 * Used for displaying rune information in champion builds and match histories.
 * Runes include keystones and secondary perks from various rune trees.
 */
public class RunePerkInfo {

    // The rune/perk unique identifier (e.g., 8010 for Conqueror)
    private final int id;

    // The rune/perk display name (e.g., "Conqueror")
    private final String name;

    // Path to the rune/perk icon image (e.g., "perk-images/Styles/Precision/Conqueror/Conqueror.png")
    private final String iconFull; // e.g., "perk-images/Styles/Precision/Conqueror/Conqueror.png"

    /**
     * Constructs a rune perk info object with all information.
     *
     * @param id The rune/perk unique identifier
     * @param name The rune/perk display name
     * @param iconFull The icon image path
     */
    public RunePerkInfo(int id, String name, String iconFull) {
        this.id = id;
        this.name = name;
        this.iconFull = iconFull;
    }

    /**
     * Gets the rune/perk ID.
     *
     * @return The rune/perk ID
     */
    public int getId() { return id; }

    /**
     * Gets the rune/perk name.
     *
     * @return The rune/perk name
     */
    public String getName() { return name; }

    /**
     * Gets the icon image path.
     *
     * @return The icon path
     */
    public String getIconFull() { return iconFull; }
}
