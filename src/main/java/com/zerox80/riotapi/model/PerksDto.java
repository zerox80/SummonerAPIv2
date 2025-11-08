package com.zerox80.riotapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Data Transfer Object representing the perks (runes) selected by a participant.
 *
 * <p>This class is a container for the list of perk styles (primary and secondary)
 * chosen by a player in a match. It provides the top-level structure for rune information
 * within a participant's data.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerksDto {
    /** A list of the perk styles (primary and secondary) selected by the participant. */
    private List<PerkStyleDto> styles;

    /**
     * Gets the list of perk styles.
     *
     * @return The list of perk styles
     */
    public List<PerkStyleDto> getStyles() { return styles; }

    /**
     * Sets the list of perk styles.
     *
     * @param styles The list of perk styles
     */
    public void setStyles(List<PerkStyleDto> styles) { this.styles = styles; }
}
