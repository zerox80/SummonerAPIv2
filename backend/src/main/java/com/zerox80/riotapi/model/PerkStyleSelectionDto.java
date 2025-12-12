// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Jackson annotation to ignore unknown JSON properties from Riot API
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Data model representing a single rune/perk selection within a rune tree.
 * Contains the rune ID and associated stat variables.
 *
 * This model maps to individual rune choices in the Match-V5 API perks data.
 * Each selection includes the perk ID (identifying which rune was chosen) and
 * three variable fields (var1, var2, var3) that contain statistics about how
 * the rune was used or what bonuses it provided during the match.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerkStyleSelectionDto {

    // The rune/perk ID (e.g., 8010 for Conqueror, 8005 for Press the Attack)
    private int perk;

    // First stat variable for this rune (usage varies by rune)
    private int var1;

    // Second stat variable for this rune (usage varies by rune)
    private int var2;

    // Third stat variable for this rune (usage varies by rune)
    private int var3;

    /**
     * Gets the perk ID.
     *
     * @return The perk ID
     */
    public int getPerk() { return perk; }

    /**
     * Sets the perk ID.
     *
     * @param perk The perk ID
     */
    public void setPerk(int perk) { this.perk = perk; }

    /**
     * Gets the first stat variable.
     *
     * @return The first variable value
     */
    public int getVar1() { return var1; }

    /**
     * Sets the first stat variable.
     *
     * @param var1 The first variable value
     */
    public void setVar1(int var1) { this.var1 = var1; }

    /**
     * Gets the second stat variable.
     *
     * @return The second variable value
     */
    public int getVar2() { return var2; }

    /**
     * Sets the second stat variable.
     *
     * @param var2 The second variable value
     */
    public void setVar2(int var2) { this.var2 = var2; }

    /**
     * Gets the third stat variable.
     *
     * @return The third variable value
     */
    public int getVar3() { return var3; }

    /**
     * Sets the third stat variable.
     *
     * @param var3 The third variable value
     */
    public void setVar3(int var3) { this.var3 = var3; }
}
