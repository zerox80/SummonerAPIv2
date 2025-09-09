package com.zerox80.riotapi.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoDto {
    private long gameCreation;
    private long gameDuration;
    private long gameEndTimestamp;
    private long gameId;
    private String gameMode;
    private String gameName;
    private long gameStartTimestamp;
    private String gameType;
    private String gameVersion;
    private int mapId;
    private List<ParticipantDto> participants;
    private String platformId;
    private int queueId;
    private String tournamentCode;
    private Integer lpChange;

    public long getGameCreation() {
        return gameCreation;
    }
    public void setGameCreation(long gameCreation) {
        this.gameCreation = gameCreation;
    }
    public long getGameDuration() {
        return gameDuration;
    }
    public void setGameDuration(long gameDuration) {
        this.gameDuration = gameDuration;
    }
    public long getGameEndTimestamp() {
        return gameEndTimestamp;
    }
    public void setGameEndTimestamp(long gameEndTimestamp) {
        this.gameEndTimestamp = gameEndTimestamp;
    }
    public long getGameId() {
        return gameId;
    }
    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
    public String getGameMode() {
        return gameMode;
    }
    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }
    public String getGameName() {
        return gameName;
    }
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    public long getGameStartTimestamp() {
        return gameStartTimestamp;
    }
    public void setGameStartTimestamp(long gameStartTimestamp) {
        this.gameStartTimestamp = gameStartTimestamp;
    }
    public String getGameType() {
        return gameType;
    }
    public void setGameType(String gameType) {
        this.gameType = gameType;
    }
    public String getGameVersion() {
        return gameVersion;
    }
    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }
    public int getMapId() {
        return mapId;
    }
    public void setMapId(int mapId) {
        this.mapId = mapId;
    }
    public List<ParticipantDto> getParticipants() {
        return participants;
    }
    public void setParticipants(List<ParticipantDto> participants) {
        this.participants = participants;
    }
    public String getPlatformId() {
        return platformId;
    }
    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }
    public int getQueueId() {
        return queueId;
    }
    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }
    public String getTournamentCode() {
        return tournamentCode;
    }
    public void setTournamentCode(String tournamentCode) {
        this.tournamentCode = tournamentCode;
    }
    public Integer getLpChange() {
        return lpChange;
    }
    public void setLpChange(Integer lpChange) {
        this.lpChange = lpChange;
    }
} 
