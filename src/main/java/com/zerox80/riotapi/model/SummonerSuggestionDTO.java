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
} 