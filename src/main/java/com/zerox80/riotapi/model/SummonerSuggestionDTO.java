package com.zerox80.riotapi.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummonerSuggestionDTO {
    
    private String riotId;
    
    private int profileIconId;
    
    private long summonerLevel;
    
    private String profileIconUrl;

    
    public SummonerSuggestionDTO(String riotId, int profileIconId, long summonerLevel) {
        this.riotId = riotId;
        this.profileIconId = profileIconId;
        this.summonerLevel = summonerLevel;
        this.profileIconUrl = null;
    }
}
