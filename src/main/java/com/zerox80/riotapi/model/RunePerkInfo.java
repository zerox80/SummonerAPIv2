package com.zerox80.riotapi.model;

/**
 * Information about a single rune perk.
 *
 * <p>This class encapsulates basic information about a single rune, including its ID,
 * name, and icon path. This is used for displaying rune information in build
 * recommendations and other UI components.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class RunePerkInfo {
    /** The unique identifier for the rune. */
    private final int id;
    /** The localized name of the rune. */
    private final String name;
    /** The path to the rune's icon image. */
    private final String iconFull; // e.g., "perk-images/Styles/Precision/Conqueror/Conqueror.png"

    /**
     * Constructs a new RunePerkInfo with the specified information.
     *
     * @param id The unique rune identifier
     * @param name The localized rune name
     * @param iconFull The path to the rune's icon image
     */
    public RunePerkInfo(int id, String name, String iconFull) {
        this.id = id;
        this.name = name;
        this.iconFull = iconFull;
    }

    /**
     * Gets the unique identifier for the rune.
     *
     * @return The rune ID
     */
    public int getId() { return id; }

    /**
     * Gets the localized name of the rune.
     *
     * @return The rune name
     */
    public String getName() { return name; }

    /**
     * Gets the path to the rune's icon image.
     *
     * @return The icon path
     */
    public String getIconFull() { return iconFull; }
}
