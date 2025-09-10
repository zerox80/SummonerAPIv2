package com.zerox80.riotapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PerkStyleDto {
    private String description; // e.g., "primaryStyle" or "subStyle"
    private int style; // tree id
    private List<PerkStyleSelectionDto> selections;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getStyle() { return style; }
    public void setStyle(int style) { this.style = style; }
    public List<PerkStyleSelectionDto> getSelections() { return selections; }
    public void setSelections(List<PerkStyleSelectionDto> selections) { this.selections = selections; }
}
