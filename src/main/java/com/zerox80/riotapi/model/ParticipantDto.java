package com.zerox80.riotapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ParticipantDto {
    private int assists;
    private String championName;
    private int championId;
    private int deaths;
    private int kills;
    private String puuid;
    private String summonerId;
    private int summonerLevel;
    private String summonerName;
    private String riotIdGameName;
    private String riotIdTagline;
    private boolean win;
    private int teamId;
    private int totalDamageDealtToChampions;
    private int totalMinionsKilled;
    private int neutralMinionsKilled;
    private int goldEarned;
    private int visionScore;
    // Additional fields for build aggregation
    private int item0;
    private int item1;
    private int item2;
    private int item3;
    private int item4;
    private int item5;
    private int item6; // trinket slot
    private int summoner1Id;
    private int summoner2Id;
    private PerksDto perks;
    private String teamPosition; // e.g., TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY

    
    public int getAssists() {
        return assists;
    }

    
    public void setAssists(int assists) {
        this.assists = assists;
    }

    
    public String getChampionName() {
        return championName;
    }

    
    public void setChampionName(String championName) {
        this.championName = championName;
    }

    
    public int getChampionId() {
        return championId;
    }

    
    public void setChampionId(int championId) {
        this.championId = championId;
    }

    
    public int getDeaths() {
        return deaths;
    }

    
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    
    public int getKills() {
        return kills;
    }

    
    public void setKills(int kills) {
        this.kills = kills;
    }

    
    public String getPuuid() {
        return puuid;
    }

    
    public void setPuuid(String puuid) {
        this.puuid = puuid;
    }

    
    public String getSummonerId() {
        return summonerId;
    }

    
    public void setSummonerId(String summonerId) {
        this.summonerId = summonerId;
    }

    
    public int getSummonerLevel() {
        return summonerLevel;
    }

    
    public void setSummonerLevel(int summonerLevel) {
        this.summonerLevel = summonerLevel;
    }

    
    public String getSummonerName() {
        return summonerName;
    }

    
    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    
    public String getRiotIdGameName() {
        return riotIdGameName;
    }

    
    public void setRiotIdGameName(String riotIdGameName) {
        this.riotIdGameName = riotIdGameName;
    }

    
    public String getRiotIdTagline() {
        return riotIdTagline;
    }

    
    public void setRiotIdTagline(String riotIdTagline) {
        this.riotIdTagline = riotIdTagline;
    }

    
    public boolean isWin() {
        return win;
    }

    
    public void setWin(boolean win) {
        this.win = win;
    }

    
    public int getTeamId() {
        return teamId;
    }

    
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    
    public int getTotalDamageDealtToChampions() {
        return totalDamageDealtToChampions;
    }

    
    public void setTotalDamageDealtToChampions(int totalDamageDealtToChampions) {
        this.totalDamageDealtToChampions = totalDamageDealtToChampions;
    }

    
    public int getTotalMinionsKilled() {
        return totalMinionsKilled;
    }

    
    public void setTotalMinionsKilled(int totalMinionsKilled) {
        this.totalMinionsKilled = totalMinionsKilled;
    }

    
    public int getNeutralMinionsKilled() {
        return neutralMinionsKilled;
    }

    
    public void setNeutralMinionsKilled(int neutralMinionsKilled) {
        this.neutralMinionsKilled = neutralMinionsKilled;
    }

    
    public int getGoldEarned() {
        return goldEarned;
    }

    
    public void setGoldEarned(int goldEarned) {
        this.goldEarned = goldEarned;
    }

    
    public int getVisionScore() {
        return visionScore;
    }

    
    public void setVisionScore(int visionScore) {
        this.visionScore = visionScore;
    }

    
    public String getKda() {
        return kills + "/" + deaths + "/" + assists;
    }

    
    public int getItem0() { return item0; }

    
    public void setItem0(int item0) { this.item0 = item0; }

    
    public int getItem1() { return item1; }

    
    public void setItem1(int item1) { this.item1 = item1; }

    
    public int getItem2() { return item2; }

    
    public void setItem2(int item2) { this.item2 = item2; }

    
    public int getItem3() { return item3; }

    
    public void setItem3(int item3) { this.item3 = item3; }

    
    public int getItem4() { return item4; }

    
    public void setItem4(int item4) { this.item4 = item4; }

    
    public int getItem5() { return item5; }

    
    public void setItem5(int item5) { this.item5 = item5; }

    
    public int getItem6() { return item6; }

    
    public void setItem6(int item6) { this.item6 = item6; }

    
    public int getSummoner1Id() { return summoner1Id; }

    
    public void setSummoner1Id(int summoner1Id) { this.summoner1Id = summoner1Id; }

    
    public int getSummoner2Id() { return summoner2Id; }

    
    public void setSummoner2Id(int summoner2Id) { this.summoner2Id = summoner2Id; }

    
    public PerksDto getPerks() { return perks; }

    
    public void setPerks(PerksDto perks) { this.perks = perks; }

    
    public String getTeamPosition() { return teamPosition; }

    
    public void setTeamPosition(String teamPosition) { this.teamPosition = teamPosition; }
}
