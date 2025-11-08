package com.zerox80.riotapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data Transfer Object representing a participant in a match.
 *
 * <p>This class encapsulates all data related to a single player's performance
 * in a match, including their stats, champion, items, and summoner information.
 * It is part of the larger InfoDto structure within a match.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
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

    /**
     * Gets the number of assists the participant had.
     *
     * @return The number of assists
     */
    public int getAssists() {
        return assists;
    }

    /**
     * Sets the number of assists the participant had.
     *
     * @param assists The number of assists
     */
    public void setAssists(int assists) {
        this.assists = assists;
    }

    /**
     * Gets the name of the champion the participant played.
     *
     * @return The champion name
     */
    public String getChampionName() {
        return championName;
    }

    /**
     * Sets the name of the champion the participant played.
     *
     * @param championName The champion name
     */
    public void setChampionName(String championName) {
        this.championName = championName;
    }

    /**
     * Gets the ID of the champion the participant played.
     *
     * @return The champion ID
     */
    public int getChampionId() {
        return championId;
    }

    /**
     * Sets the ID of the champion the participant played.
     *
     * @param championId The champion ID
     */
    public void setChampionId(int championId) {
        this.championId = championId;
    }

    /**
     * Gets the number of deaths the participant had.
     *
     * @return The number of deaths
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * Sets the number of deaths the participant had.
     *
     * @param deaths The number of deaths
     */
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    /**
     * Gets the number of kills the participant had.
     *
     * @return The number of kills
     */
    public int getKills() {
        return kills;
    }

    /**
     * Sets the number of kills the participant had.
     *
     * @param kills The number of kills
     */
    public void setKills(int kills) {
        this.kills = kills;
    }

    /**
     * Gets the participant's PUUID.
     *
     * @return The PUUID
     */
    public String getPuuid() {
        return puuid;
    }

    /**
     * Sets the participant's PUUID.
     *
     * @param puuid The PUUID
     */
    public void setPuuid(String puuid) {
        this.puuid = puuid;
    }

    /**
     * Gets the participant's summoner ID.
     *
     * @return The summoner ID
     */
    public String getSummonerId() {
        return summonerId;
    }

    /**
     * Sets the participant's summoner ID.
     *
     * @param summonerId The summoner ID
     */
    public void setSummonerId(String summonerId) {
        this.summonerId = summonerId;
    }

    /**
     * Gets the participant's summoner level.
     *
     * @return The summoner level
     */
    public int getSummonerLevel() {
        return summonerLevel;
    }

    /**
     * Sets the participant's summoner level.
     *
     * @param summonerLevel The summoner level
     */
    public void setSummonerLevel(int summonerLevel) {
        this.summonerLevel = summonerLevel;
    }

    /**
     * Gets the participant's summoner name.
     *
     * @return The summoner name
     */
    public String getSummonerName() {
        return summonerName;
    }

    /**
     * Sets the participant's summoner name.
     *
     * @param summonerName The summoner name
     */
    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    /**
     * Gets the game name part of the participant's Riot ID.
     *
     * @return The Riot ID game name
     */
    public String getRiotIdGameName() {
        return riotIdGameName;
    }

    /**
     * Sets the game name part of the participant's Riot ID.
     *
     * @param riotIdGameName The Riot ID game name
     */
    public void setRiotIdGameName(String riotIdGameName) {
        this.riotIdGameName = riotIdGameName;
    }

    /**
     * Gets the tagline part of the participant's Riot ID.
     *
     * @return The Riot ID tagline
     */
    public String getRiotIdTagline() {
        return riotIdTagline;
    }

    /**
     * Sets the tagline part of the participant's Riot ID.
     *
     * @param riotIdTagline The Riot ID tagline
     */
    public void setRiotIdTagline(String riotIdTagline) {
        this.riotIdTagline = riotIdTagline;
    }

    /**
     * Checks if the participant's team won the match.
     *
     * @return True if the team won, false otherwise
     */
    public boolean isWin() {
        return win;
    }

    /**
     * Sets whether the participant's team won the match.
     *
     * @param win True if the team won, false otherwise
     */
    public void setWin(boolean win) {
        this.win = win;
    }

    /**
     * Gets the ID of the team the participant was on.
     *
     * @return The team ID
     */
    public int getTeamId() {
        return teamId;
    }

    /**
     * Sets the ID of the team the participant was on.
     *
     * @param teamId The team ID
     */
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    /**
     * Gets the total damage dealt to champions by the participant.
     *
     * @return The total damage dealt to champions
     */
    public int getTotalDamageDealtToChampions() {
        return totalDamageDealtToChampions;
    }

    /**
     * Sets the total damage dealt to champions by the participant.
     *
     * @param totalDamageDealtToChampions The total damage dealt to champions
     */
    public void setTotalDamageDealtToChampions(int totalDamageDealtToChampions) {
        this.totalDamageDealtToChampions = totalDamageDealtToChampions;
    }

    /**
     * Gets the total number of minions killed by the participant.
     *
     * @return The total minions killed
     */
    public int getTotalMinionsKilled() {
        return totalMinionsKilled;
    }

    /**
     * Sets the total number of minions killed by the participant.
     *
     * @param totalMinionsKilled The total minions killed
     */
    public void setTotalMinionsKilled(int totalMinionsKilled) {
        this.totalMinionsKilled = totalMinionsKilled;
    }

    /**
     * Gets the number of neutral minions killed by the participant.
     *
     * @return The neutral minions killed
     */
    public int getNeutralMinionsKilled() {
        return neutralMinionsKilled;
    }

    /**
     * Sets the number of neutral minions killed by the participant.
     *
     * @param neutralMinionsKilled The neutral minions killed
     */
    public void setNeutralMinionsKilled(int neutralMinionsKilled) {
        this.neutralMinionsKilled = neutralMinionsKilled;
    }

    /**
     * Gets the total gold earned by the participant.
     *
     * @return The gold earned
     */
    public int getGoldEarned() {
        return goldEarned;
    }

    /**
     * Sets the total gold earned by the participant.
     *
     * @param goldEarned The gold earned
     */
    public void setGoldEarned(int goldEarned) {
        this.goldEarned = goldEarned;
    }

    /**
     * Gets the vision score of the participant.
     *
     * @return The vision score
     */
    public int getVisionScore() {
        return visionScore;
    }

    /**
     * Sets the vision score of the participant.
     *
     * @param visionScore The vision score
     */
    public void setVisionScore(int visionScore) {
        this.visionScore = visionScore;
    }

    /**
     * Gets the participant's KDA (Kills/Deaths/Assists) as a string.
     *
     * @return The KDA string
     */
    public String getKda() {
        return kills + "/" + deaths + "/" + assists;
    }

    /**
     * Gets the ID of the item in the first slot.
     *
     * @return The item ID
     */
    public int getItem0() { return item0; }

    /**
     * Sets the ID of the item in the first slot.
     *
     * @param item0 The item ID
     */
    public void setItem0(int item0) { this.item0 = item0; }

    /**
     * Gets the ID of the item in the second slot.
     *
     * @return The item ID
     */
    public int getItem1() { return item1; }

    /**
     * Sets the ID of the item in the second slot.
     *
     * @param item1 The item ID
     */
    public void setItem1(int item1) { this.item1 = item1; }

    /**
     * Gets the ID of the item in the third slot.
     *
     * @return The item ID
     */
    public int getItem2() { return item2; }

    /**
     * Sets the ID of the item in the third slot.
     *
     * @param item2 The item ID
     */
    public void setItem2(int item2) { this.item2 = item2; }

    /**
     * Gets the ID of the item in the fourth slot.
     *
     * @return The item ID
     */
    public int getItem3() { return item3; }

    /**
     * Sets the ID of the item in the fourth slot.
     *
     * @param item3 The item ID
     */
    public void setItem3(int item3) { this.item3 = item3; }

    /**
     * Gets the ID of the item in the fifth slot.
     *
     * @return The item ID
     */
    public int getItem4() { return item4; }

    /**
     * Sets the ID of the item in the fifth slot.
     *
     * @param item4 The item ID
     */
    public void setItem4(int item4) { this.item4 = item4; }

    /**
     * Gets the ID of the item in the sixth slot.
     *
     * @return The item ID
     */
    public int getItem5() { return item5; }

    /**
     * Sets the ID of the item in the sixth slot.
     *
     * @param item5 The item ID
     */
    public void setItem5(int item5) { this.item5 = item5; }

    /**
     * Gets the ID of the item in the trinket slot.
     *
     * @return The item ID
     */
    public int getItem6() { return item6; }

    /**
     * Sets the ID of the item in the trinket slot.
     *
     * @param item6 The item ID
     */
    public void setItem6(int item6) { this.item6 = item6; }

    /**
     * Gets the ID of the first summoner spell.
     *
     * @return The summoner spell ID
     */
    public int getSummoner1Id() { return summoner1Id; }

    /**
     * Sets the ID of the first summoner spell.
     *
     * @param summoner1Id The summoner spell ID
     */
    public void setSummoner1Id(int summoner1Id) { this.summoner1Id = summoner1Id; }

    /**
     * Gets the ID of the second summoner spell.
     *
     * @return The summoner spell ID
     */
    public int getSummoner2Id() { return summoner2Id; }

    /**
     * Sets the ID of the second summoner spell.
     *
     * @param summoner2Id The summoner spell ID
     */
    public void setSummoner2Id(int summoner2Id) { this.summoner2Id = summoner2Id; }

    /**
     * Gets the participant's perks (runes).
     *
     * @return The perks DTO
     */
    public PerksDto getPerks() { return perks; }

    /**
     * Sets the participant's perks (runes).
     *
     * @param perks The perks DTO
     */
    public void setPerks(PerksDto perks) { this.perks = perks; }

    /**
     * Gets the participant's team position (role).
     *
     * @return The team position
     */
    public String getTeamPosition() { return teamPosition; }

    /**
     * Sets the participant's team position (role).
     *
     * @param teamPosition The team position
     */
    public void setTeamPosition(String teamPosition) { this.teamPosition = teamPosition; }
}
