package com.zerox80.riotapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data Transfer Object representing a single perk (rune) selection.
 *
 * <p>This class encapsulates a single perk selected by a participant in a match,
 * including the perk's ID and three variables that represent perk-specific values
 * (e.g., damage dealt, healing done).</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerkStyleSelectionDto {
    /** The ID of the selected perk (rune). */
    private int perk;
    /** The first perk-specific variable. */
    private int var1;
    /** The second perk-specific variable. */
    private int var2;
    /** The third perk-specific variable. */
    private int var3;

    /**
     * Gets the ID of the selected perk.
     *
     * @return The perk ID
     */
    public int getPerk() { return perk; }

    /**
     * Sets the ID of the selected perk.
     *
     * @param perk The perk ID
     */
    public void setPerk(int perk) { this.perk = perk; }

    /**
     * Gets the first perk-specific variable.
     *
     * @return The first variable
     */
    public int getVar1() { return var1; }

    /**
     * Sets the first perk-specific variable.
     *
     * @param var1 The first variable
     */
    public void setVar1(int var1) { this.var1 = var1; }

    /**
     * Gets the second perk-specific variable.
     *
     * @return The second variable
     */
    public int getVar2() { return var2; }

    /**
     * Sets the second perk-specific variable.
     *
     * @param var2 The second variable
     */
    public void setVar2(int var2) { this.var2 = var2; }

    /**
     * Gets the third perk-specific variable.
     *
     * @return The third variable
     */
    public int getVar3() { return var3; }

    /**
     * Sets the third perk-specific variable.
     *
     * @param var3 The third variable
     */
    public void setVar3(int var3) { this.var3 = var3; }
}
