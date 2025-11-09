package com.zerox80.riotapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PerksDto {
    
    private List<PerkStyleDto> styles;

    
    public List<PerkStyleDto> getStyles() { return styles; }

    
    public void setStyles(List<PerkStyleDto> styles) { this.styles = styles; }
}
