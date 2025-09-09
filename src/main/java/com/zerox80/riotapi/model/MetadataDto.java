package com.zerox80.riotapi.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataDto {
    private String dataVersion;
    private String matchId;
    private List<String> participants;

    public String getDataVersion() {
        return dataVersion;
    }
    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }
    public String getMatchId() {
        return matchId;
    }
    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }
    public List<String> getParticipants() {
        return participants;
    }
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
} 