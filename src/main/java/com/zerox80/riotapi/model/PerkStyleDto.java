// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Jackson annotation to ignore unknown JSON properties from Riot API
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// Import for Java List collection to hold perk selections
import java.util.List;


/**
 * Data model representing a single rune style (primary or secondary rune tree) in a match.
 * Contains the style ID and the specific runes selected from that tree.
 *
 * This model maps to individual rune tree selections in the Match-V5 API perks data.
 * Each player has two perk styles: a primary tree (with 4 rune selections) and a
 * secondary tree (with 2 rune selections). The style ID identifies which rune tree
 * was used (e.g., Precision, Domination, etc.).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerkStyleDto {

    // Description of the rune style (often "primaryStyle" or "subStyle")
    private String description;

    // The rune tree/style ID (e.g., 8000 for Precision, 8100 for Domination)
    private int style;

    // List of specific runes selected from this tree
    private List<PerkStyleSelectionDto> selections;

    /**
     * Gets the style description.
     *
     * @return The description string
     */
    public String getDescription() { return description; }

    /**
     * Sets the style description.
     *
     * @param description The description string
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets the rune tree/style ID.
     *
     * @return The style ID
     */
    public int getStyle() { return style; }

    /**
     * Sets the rune tree/style ID.
     *
     * @param style The style ID
     */
    public void setStyle(int style) { this.style = style; }

    /**
     * Gets the list of rune selections from this tree.
     *
     * @return List of perk selections
     */
    public List<PerkStyleSelectionDto> getSelections() { return selections; }

    /**
     * Sets the list of rune selections.
     *
     * @param selections List of perk selections
     */
    public void setSelections(List<PerkStyleSelectionDto> selections) { this.selections = selections; }
}
