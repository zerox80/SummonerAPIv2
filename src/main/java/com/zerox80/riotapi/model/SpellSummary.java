// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Java List collection to hold notes
import java.util.List;


/**
 * Data model representing a champion ability/spell with detailed information.
 * Contains comprehensive data about a champion's Q, W, E, or R ability.
 *
 * This model provides all information needed to display a champion ability detail page,
 * including name, tooltip, cooldown, cost, range, damage values, and scaling information.
 * Spell values are stored as strings to allow simple rendering without parsing complex
 * formulas (e.g., "12/11/10/9/8 s" for cooldown at each rank).
 */
public class SpellSummary {

    // The spell's unique identifier key (e.g., "FlashFrost" for Anivia Q)
    private final String id;

    // The spell's display name (e.g., "Flash Frost")
    private final String name;

    // The spell's detailed description/tooltip explaining what it does
    private String tooltip;

    // Filename of the spell's icon image
    private final String imageFull;

    // Optional values stored as strings for simple rendering without strict parsing

    // Cooldown at each rank (e.g., "12/11/10/9/8 s")
    private String cooldown;   // e.g., "12/11/10/9/8 s"

    // Resource cost at each rank (e.g., "80/90/100/110/120 Mana")
    private String cost;       // e.g., "80/90/100/110/120 Mana"

    // Cast range (e.g., "1100")
    private String range;      // e.g., "1100"

    // Damage values at each rank (e.g., "80/120/160/200/240 (+45% AP)")
    private String damage;     // e.g., "80/120/160/200/240 (+45% AP)"

    // Scaling information (e.g., "Scales with Ability Power")
    private String scaling;    // e.g., "Skaliert mit Fähigkeitsstärke"

    // Additional notes or bullet points about the spell
    private List<String> notes; // bullet points

    /**
     * Constructs a spell summary with core information.
     *
     * @param id The spell's unique identifier
     * @param name The spell's display name
     * @param tooltip The spell's description
     * @param imageFull The image filename
     */
    public SpellSummary(String id, String name, String tooltip, String imageFull) {
        this.id = id;
        this.name = name;
        this.tooltip = tooltip;
        this.imageFull = imageFull;
    }

    /**
     * Gets the spell ID.
     *
     * @return The spell ID
     */
    public String getId() { return id; }

    /**
     * Gets the spell name.
     *
     * @return The spell name
     */
    public String getName() { return name; }

    /**
     * Gets the spell tooltip/description.
     *
     * @return The tooltip
     */
    public String getTooltip() { return tooltip; }

    /**
     * Sets the spell tooltip/description.
     *
     * @param tooltip The tooltip
     */
    public void setTooltip(String tooltip) { this.tooltip = tooltip; }

    /**
     * Gets the image filename.
     *
     * @return The image filename
     */
    public String getImageFull() { return imageFull; }

    /**
     * Gets the cooldown string.
     *
     * @return The cooldown values
     */
    public String getCooldown() { return cooldown; }

    /**
     * Sets the cooldown string.
     *
     * @param cooldown The cooldown values
     */
    public void setCooldown(String cooldown) { this.cooldown = cooldown; }

    /**
     * Gets the cost string.
     *
     * @return The cost values
     */
    public String getCost() { return cost; }

    /**
     * Sets the cost string.
     *
     * @param cost The cost values
     */
    public void setCost(String cost) { this.cost = cost; }

    /**
     * Gets the range string.
     *
     * @return The range value
     */
    public String getRange() { return range; }

    /**
     * Sets the range string.
     *
     * @param range The range value
     */
    public void setRange(String range) { this.range = range; }

    /**
     * Gets the damage string.
     *
     * @return The damage values
     */
    public String getDamage() { return damage; }

    /**
     * Sets the damage string.
     *
     * @param damage The damage values
     */
    public void setDamage(String damage) { this.damage = damage; }

    /**
     * Gets the scaling string.
     *
     * @return The scaling description
     */
    public String getScaling() { return scaling; }

    /**
     * Sets the scaling string.
     *
     * @param scaling The scaling description
     */
    public void setScaling(String scaling) { this.scaling = scaling; }

    /**
     * Gets the list of additional notes.
     *
     * @return List of notes
     */
    public List<String> getNotes() { return notes; }

    /**
     * Sets the list of additional notes.
     *
     * @param notes List of notes
     */
    public void setNotes(List<String> notes) { this.notes = notes; }
}
