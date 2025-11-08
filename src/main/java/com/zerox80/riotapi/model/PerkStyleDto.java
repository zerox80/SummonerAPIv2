package com.zerox80.riotapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Data Transfer Object representing a style of perks (runes).
 *
 * <p>This class encapsulates a single style of perks (either primary or secondary)
 * selected by a participant in a match. It includes the ID of the style (rune tree)
 * and a list of the specific perks (runes) selected within that style.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerkStyleDto {
    /** A description of the style (e.g., "primaryStyle", "subStyle"). */
    private String description;
    /** The ID of the perk style (rune tree). */
    private int style;
    /** A list of the perks selected within this style. */
    private List<PerkStyleSelectionDto> selections;

    /**
     * Gets the description of the perk style.
     *
     * @return The description
     */
    public String getDescription() { return description; }

    /**
     * Sets the description of the perk style.
     *
     * @param description The description
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets the ID of the perk style (rune tree).
     *
     * @return The style ID
     */
    public int getStyle() { return style; }

    /**
     * Sets the ID of the perk style (rune tree).
     *
     * @param style The style ID
     */
    public void setStyle(int style) { this.style = style; }

    /**
     * Gets the list of selected perks within this style.
     *
     * @return The list of perk selections
     */
    public List<PerkStyleSelectionDto> getSelections() { return selections; }

    /**
     * Sets the list of selected perks within this style.
     *
     * @param selections The list of perk selections
     */
    public void setSelections(List<PerkStyleSelectionDto> selections) { this.selections = selections; }
}
