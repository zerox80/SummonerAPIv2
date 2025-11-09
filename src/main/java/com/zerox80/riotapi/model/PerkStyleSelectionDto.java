package com.zerox80.riotapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PerkStyleSelectionDto {
    
    private int perk;
    
    private int var1;
    
    private int var2;
    
    private int var3;

    
    public int getPerk() { return perk; }

    
    public void setPerk(int perk) { this.perk = perk; }

    
    public int getVar1() { return var1; }

    
    public void setVar1(int var1) { this.var1 = var1; }

    
    public int getVar2() { return var2; }

    
    public void setVar2(int var2) { this.var2 = var2; }

    
    public int getVar3() { return var3; }

    
    public void setVar3(int var3) { this.var3 = var3; }
}
