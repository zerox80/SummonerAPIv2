// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Jackson annotation to ignore unknown JSON properties from Riot API
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// Import for Java List collection to hold rune styles
import java.util.List;


/**
 * Data model representing a player's rune/perk configuration in a match.
 * Contains the complete rune setup including primary and secondary rune trees.
 *
 * This model maps to the "perks" section of participant data in the Match-V5 API.
 * It contains a list of perk styles (typically 2: primary and secondary rune trees),
 * each with their selected runes. Used to track and display what runes players
 * used in matches.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerksDto {

    // List of perk styles (primary and secondary rune trees)
    private List<PerkStyleDto> styles;

    /**
     * Gets the list of perk styles.
     *
     * @return List of perk styles (primary and secondary)
     */
    public List<PerkStyleDto> getStyles() { return styles; }

    /**
     * Sets the list of perk styles.
     *
     * @param styles List of perk styles
     */
    public void setStyles(List<PerkStyleDto> styles) { this.styles = styles; }
}
