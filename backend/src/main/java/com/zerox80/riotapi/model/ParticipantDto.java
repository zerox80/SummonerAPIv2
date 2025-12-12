// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Jackson annotation to ignore unknown JSON properties from Riot API
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Data model representing a single participant/player in a League of Legends match.
 * Contains comprehensive statistics and build information for one player in a game.
 *
 * This model maps to participant data in the Match-V5 API and includes player identity,
 * champion played, performance stats (KDA, damage, CS, vision), build information
 * (items, summoner spells, runes), and match outcome. The JsonIgnoreProperties annotation
 * allows graceful handling of new fields added by Riot without breaking the application.
 *
 * This class includes fields for build aggregation to help generate champion build
 * recommendations from high-ELO match data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParticipantDto {
    // Performance statistics
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
     * Gets the number of assists.
     *
     * @return Assist count
     */
    public int getAssists() {
        return assists;
    }

    /**
     * Sets the number of assists.
     *
     * @param assists Assist count
     */
    public void setAssists(int assists) {
        this.assists = assists;
    }

    /**
     * Gets the champion name.
     *
     * @return Champion name (e.g., "Aatrox")
     */
    public String getChampionName() {
        return championName;
    }

    /**
     * Sets the champion name.
     *
     * @param championName Champion name
     */
    public void setChampionName(String championName) {
        this.championName = championName;
    }

    /**
     * Gets the champion ID.
     *
     * @return Numeric champion ID
     */
    public int getChampionId() {
        return championId;
    }

    /**
     * Sets the champion ID.
     *
     * @param championId Numeric champion ID
     */
    public void setChampionId(int championId) {
        this.championId = championId;
    }

    /**
     * Gets the number of deaths.
     *
     * @return Death count
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * Sets the number of deaths.
     *
     * @param deaths Death count
     */
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    /**
     * Gets the number of kills.
     *
     * @return Kill count
     */
    public int getKills() {
        return kills;
    }

    /**
     * Sets the number of kills.
     *
     * @param kills Kill count
     */
    public void setKills(int kills) {
        this.kills = kills;
    }

    /**
     * Gets the player's PUUID.
     *
     * @return The PUUID
     */
    public String getPuuid() {
        return puuid;
    }

    /**
     * Sets the player's PUUID.
     *
     * @param puuid The PUUID
     */
    public void setPuuid(String puuid) {
        this.puuid = puuid;
    }

    /**
     * Gets the encrypted summoner ID.
     *
     * @return The summoner ID
     */
    public String getSummonerId() {
        return summonerId;
    }

    /**
     * Sets the encrypted summoner ID.
     *
     * @param summonerId The summoner ID
     */
    public void setSummonerId(String summonerId) {
        this.summonerId = summonerId;
    }

    /**
     * Gets the summoner level.
     *
     * @return The summoner level
     */
    public int getSummonerLevel() {
        return summonerLevel;
    }

    /**
     * Sets the summoner level.
     *
     * @param summonerLevel The summoner level
     */
    public void setSummonerLevel(int summonerLevel) {
        this.summonerLevel = summonerLevel;
    }

    /**
     * Gets the summoner name (legacy field).
     *
     * @return The summoner name
     */
    public String getSummonerName() {
        return summonerName;
    }

    /**
     * Sets the summoner name.
     *
     * @param summonerName The summoner name
     */
    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    /**
     * Gets the Riot ID game name (the name part before #).
     *
     * @return The game name
     */
    public String getRiotIdGameName() {
        return riotIdGameName;
    }

    /**
     * Sets the Riot ID game name.
     *
     * @param riotIdGameName The game name
     */
    public void setRiotIdGameName(String riotIdGameName) {
        this.riotIdGameName = riotIdGameName;
    }

    /**
     * Gets the Riot ID tagline (the tag after #).
     *
     * @return The tagline
     */
    public String getRiotIdTagline() {
        return riotIdTagline;
    }

    /**
     * Sets the Riot ID tagline.
     *
     * @param riotIdTagline The tagline
     */
    public void setRiotIdTagline(String riotIdTagline) {
        this.riotIdTagline = riotIdTagline;
    }

    /**
     * Checks if this participant won the game.
     *
     * @return true if won, false if lost
     */
    public boolean isWin() {
        return win;
    }

    /**
     * Sets the win status.
     *
     * @param win true if won, false if lost
     */
    public void setWin(boolean win) {
        this.win = win;
    }

    /**
     * Gets the team ID (100 for blue side, 200 for red side).
     *
     * @return The team ID
     */
    public int getTeamId() {
        return teamId;
    }

    /**
     * Sets the team ID.
     *
     * @param teamId The team ID
     */
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    /**
     * Gets total damage dealt to champions.
     *
     * @return Damage to champions
     */
    public int getTotalDamageDealtToChampions() {
        return totalDamageDealtToChampions;
    }

    /**
     * Sets total damage dealt to champions.
     *
     * @param totalDamageDealtToChampions Damage to champions
     */
    public void setTotalDamageDealtToChampions(int totalDamageDealtToChampions) {
        this.totalDamageDealtToChampions = totalDamageDealtToChampions;
    }

    /**
     * Gets total minions (CS) killed.
     *
     * @return Minion kills
     */
    public int getTotalMinionsKilled() {
        return totalMinionsKilled;
    }

    /**
     * Sets total minions killed.
     *
     * @param totalMinionsKilled Minion kills
     */
    public void setTotalMinionsKilled(int totalMinionsKilled) {
        this.totalMinionsKilled = totalMinionsKilled;
    }

    /**
     * Gets neutral minions (jungle monsters) killed.
     *
     * @return Neutral minion kills
     */
    public int getNeutralMinionsKilled() {
        return neutralMinionsKilled;
    }

    /**
     * Sets neutral minions killed.
     *
     * @param neutralMinionsKilled Neutral minion kills
     */
    public void setNeutralMinionsKilled(int neutralMinionsKilled) {
        this.neutralMinionsKilled = neutralMinionsKilled;
    }

    /**
     * Gets total gold earned.
     *
     * @return Gold earned
     */
    public int getGoldEarned() {
        return goldEarned;
    }

    /**
     * Sets total gold earned.
     *
     * @param goldEarned Gold earned
     */
    public void setGoldEarned(int goldEarned) {
        this.goldEarned = goldEarned;
    }

    /**
     * Gets the vision score.
     *
     * @return Vision score
     */
    public int getVisionScore() {
        return visionScore;
    }

    /**
     * Sets the vision score.
     *
     * @param visionScore Vision score
     */
    public void setVisionScore(int visionScore) {
        this.visionScore = visionScore;
    }

    /**
     * Gets the KDA string in "K/D/A" format.
     *
     * @return KDA string (e.g., "10/3/15")
     */
    public String getKda() {
        return kills + "/" + deaths + "/" + assists;
    }

    /**
     * Gets item in slot 0.
     *
     * @return Item ID, or 0 if empty
     */
    public int getItem0() { return item0; }

    /**
     * Sets item in slot 0.
     *
     * @param item0 Item ID
     */
    public void setItem0(int item0) { this.item0 = item0; }

    /**
     * Gets item in slot 1.
     *
     * @return Item ID, or 0 if empty
     */
    public int getItem1() { return item1; }

    /**
     * Sets item in slot 1.
     *
     * @param item1 Item ID
     */
    public void setItem1(int item1) { this.item1 = item1; }

    /**
     * Gets item in slot 2.
     *
     * @return Item ID, or 0 if empty
     */
    public int getItem2() { return item2; }

    /**
     * Sets item in slot 2.
     *
     * @param item2 Item ID
     */
    public void setItem2(int item2) { this.item2 = item2; }

    /**
     * Gets item in slot 3.
     *
     * @return Item ID, or 0 if empty
     */
    public int getItem3() { return item3; }

    /**
     * Sets item in slot 3.
     *
     * @param item3 Item ID
     */
    public void setItem3(int item3) { this.item3 = item3; }

    /**
     * Gets item in slot 4.
     *
     * @return Item ID, or 0 if empty
     */
    public int getItem4() { return item4; }

    /**
     * Sets item in slot 4.
     *
     * @param item4 Item ID
     */
    public void setItem4(int item4) { this.item4 = item4; }

    /**
     * Gets item in slot 5.
     *
     * @return Item ID, or 0 if empty
     */
    public int getItem5() { return item5; }

    /**
     * Sets item in slot 5.
     *
     * @param item5 Item ID
     */
    public void setItem5(int item5) { this.item5 = item5; }

    /**
     * Gets item in slot 6 (trinket slot).
     *
     * @return Item ID, or 0 if empty
     */
    public int getItem6() { return item6; }

    /**
     * Sets item in slot 6 (trinket slot).
     *
     * @param item6 Item ID
     */
    public void setItem6(int item6) { this.item6 = item6; }

    /**
     * Gets the first summoner spell ID.
     *
     * @return Summoner spell ID (e.g., 4 for Flash)
     */
    public int getSummoner1Id() { return summoner1Id; }

    /**
     * Sets the first summoner spell ID.
     *
     * @param summoner1Id Summoner spell ID
     */
    public void setSummoner1Id(int summoner1Id) { this.summoner1Id = summoner1Id; }

    /**
     * Gets the second summoner spell ID.
     *
     * @return Summoner spell ID (e.g., 14 for Ignite)
     */
    public int getSummoner2Id() { return summoner2Id; }

    /**
     * Sets the second summoner spell ID.
     *
     * @param summoner2Id Summoner spell ID
     */
    public void setSummoner2Id(int summoner2Id) { this.summoner2Id = summoner2Id; }

    /**
     * Gets the rune/perk configuration.
     *
     * @return Perks object containing rune setup
     */
    public PerksDto getPerks() { return perks; }

    /**
     * Sets the rune/perk configuration.
     *
     * @param perks Perks object
     */
    public void setPerks(PerksDto perks) { this.perks = perks; }

    /**
     * Gets the team position/role.
     *
     * @return Position (TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY)
     */
    public String getTeamPosition() { return teamPosition; }

    /**
     * Sets the team position/role.
     *
     * @param teamPosition Position string
     */
    public void setTeamPosition(String teamPosition) { this.teamPosition = teamPosition; }
}
