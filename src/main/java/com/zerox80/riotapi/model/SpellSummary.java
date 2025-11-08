package com.zerox80.riotapi.model;

import java.util.List;

/**
 * Summary information about a champion's spell (ability).
 *
 * <p>This class encapsulates basic information about a champion's spell,
 * including its ID, name, tooltip, and image. It also includes optional
 * fields for spell details like cooldown, cost, and range.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class SpellSummary {
    /** The unique identifier for the spell. */
    private final String id;
    /** The localized name of the spell. */
    private final String name;
    /** The localized tooltip (description) of the spell. */
    private String tooltip;
    /** The filename for the spell's image. */
    private final String imageFull;

    // Optional values (strings to allow simple rendering without strict parsing)
    /** The cooldown of the spell. */
    private String cooldown;   // e.g., "12/11/10/9/8 s"
    /** The cost of the spell. */
    private String cost;       // e.g., "80/90/100/110/120 Mana"
    /** The range of the spell. */
    private String range;      // e.g., "1100"
    /** The damage of the spell. */
    private String damage;     // e.g., "80/120/160/200/240 (+45% AP)"
    /** The scaling of the spell. */
    private String scaling;    // e.g., "Skaliert mit Fähigkeitsstärke"
    /** A list of notes about the spell. */
    private List<String> notes; // bullet points

    /**
     * Constructs a new SpellSummary with the specified information.
     *
     * @param id The unique spell identifier
     * @param name The localized spell name
     * @param tooltip The localized spell tooltip
     * @param imageFull The filename for the spell's image
     */
    public SpellSummary(String id, String name, String tooltip, String imageFull) {
        this.id = id;
        this.name = name;
        this.tooltip = tooltip;
        this.imageFull = imageFull;
    }

    /**
     * Gets the unique identifier for the spell.
     *
     * @return The spell ID
     */
    public String getId() { return id; }

    /**
     * Gets the localized name of the spell.
     *
     * @return The spell name
     */
    public String getName() { return name; }

    /**
     * Gets the localized tooltip of the spell.
     *
     * @return The spell tooltip
     */
    public String getTooltip() { return tooltip; }

    /**
     * Sets the localized tooltip of the spell.
     *
     * @param tooltip The spell tooltip
     */
    public void setTooltip(String tooltip) { this.tooltip = tooltip; }

    /**
     * Gets the filename for the spell's image.
     *
     * @return The image filename
     */
    public String getImageFull() { return imageFull; }

    /**
     * Gets the cooldown of the spell.
     *
     * @return The cooldown string
     */
    public String getCooldown() { return cooldown; }

    /**
     * Sets the cooldown of the spell.
     *
     * @param cooldown The cooldown string
     */
    public void setCooldown(String cooldown) { this.cooldown = cooldown; }

    /**
     * Gets the cost of the spell.
     *
     * @return The cost string
     */
    public String getCost() { return cost; }

    /**
     * Sets the cost of the spell.
     *
     * @param cost The cost string
     */
    public void setCost(String cost) { this.cost = cost; }

    /**
     * Gets the range of the spell.
     *
     * @return The range string
     */
    public String getRange() { return range; }

    /**
     * Sets the range of the spell.
     *
     * @param range The range string
     */
    public void setRange(String range) { this.range = range; }

    /**
     * Gets the damage of the spell.
     *
     * @return The damage string
     */
    public String getDamage() { return damage; }

    /**
     * Sets the damage of the spell.
     *
     * @param damage The damage string
     */
    public void setDamage(String damage) { this.damage = damage; }

    /**
     * Gets the scaling of the spell.
     *
     * @return The scaling string
     */
    public String getScaling() { return scaling; }

    /**
     * Sets the scaling of the spell.
     *
     * @param scaling The scaling string
     */
    public void setScaling(String scaling) { this.scaling = scaling; }

    /**
     * Gets the list of notes about the spell.
     *
     * @return The list of notes
     */
    public List<String> getNotes() { return notes; }

    /**
     * Sets the list of notes about the spell.
     *
     * @param notes The list of notes
     */
    public void setNotes(List<String> notes) { this.notes = notes; }
}
