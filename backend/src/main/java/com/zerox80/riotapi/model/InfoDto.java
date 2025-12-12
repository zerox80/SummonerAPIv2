// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Java List collection to hold participant data
import java.util.List;
// Import for Jackson annotation to ignore unknown JSON properties from Riot API
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Data model representing detailed match information from Riot's Match-V5 API.
 * Contains comprehensive game metadata and participant data for a completed match.
 *
 * This model maps to the "info" section of the Match-V5 API response and includes
 * game timing information, mode/type, participants list, and other match metadata.
 * The JsonIgnoreProperties annotation allows graceful handling of new fields added
 * by Riot without breaking the application.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoDto {

    // Unix timestamp (milliseconds) when the game was created/entered queue
    private long gameCreation;

    // Game duration in seconds
    private long gameDuration;

    // Unix timestamp (milliseconds) when the game ended
    private long gameEndTimestamp;

    // Unique identifier for this specific game instance
    private long gameId;

    // Game mode (e.g., "CLASSIC", "ARAM")
    private String gameMode;

    // Human-readable game name (often empty for standard games)
    private String gameName;

    // Unix timestamp (milliseconds) when the game actually started
    private long gameStartTimestamp;

    // Game type (e.g., "MATCHED_GAME", "CUSTOM_GAME")
    private String gameType;

    // Game version/patch (e.g., "14.23.591.1327")
    private String gameVersion;

    // Map ID (e.g., 11 for Summoner's Rift, 12 for Howling Abyss)
    private int mapId;

    // List of all 10 participants in the game with their detailed stats
    private List<ParticipantDto> participants;

    // Platform/region identifier (e.g., "NA1", "EUW1")
    private String platformId;

    // Queue ID (e.g., 420 for Solo/Duo Ranked, 440 for Flex Ranked)
    private int queueId;

    // Tournament code if this was a tournament game, otherwise null
    private String tournamentCode;

    // LP (League Points) change for this game (calculated by application, not from Riot API)
    private Integer lpChange;

    /**
     * Gets the game creation timestamp.
     *
     * @return Unix timestamp in milliseconds
     */
    public long getGameCreation() {
        return gameCreation;
    }

    /**
     * Sets the game creation timestamp.
     *
     * @param gameCreation Unix timestamp in milliseconds
     */
    public void setGameCreation(long gameCreation) {
        this.gameCreation = gameCreation;
    }

    /**
     * Gets the game duration.
     *
     * @return Duration in seconds
     */
    public long getGameDuration() {
        return gameDuration;
    }

    /**
     * Sets the game duration.
     *
     * @param gameDuration Duration in seconds
     */
    public void setGameDuration(long gameDuration) {
        this.gameDuration = gameDuration;
    }

    /**
     * Gets the game end timestamp.
     *
     * @return Unix timestamp in milliseconds
     */
    public long getGameEndTimestamp() {
        return gameEndTimestamp;
    }

    /**
     * Sets the game end timestamp.
     *
     * @param gameEndTimestamp Unix timestamp in milliseconds
     */
    public void setGameEndTimestamp(long gameEndTimestamp) {
        this.gameEndTimestamp = gameEndTimestamp;
    }

    /**
     * Gets the game ID.
     *
     * @return The game ID
     */
    public long getGameId() {
        return gameId;
    }

    /**
     * Sets the game ID.
     *
     * @param gameId The game ID
     */
    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    /**
     * Gets the game mode.
     *
     * @return The game mode
     */
    public String getGameMode() {
        return gameMode;
    }

    /**
     * Sets the game mode.
     *
     * @param gameMode The game mode
     */
    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * Gets the game name.
     *
     * @return The game name
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Sets the game name.
     *
     * @param gameName The game name
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Gets the game start timestamp.
     *
     * @return Unix timestamp in milliseconds
     */
    public long getGameStartTimestamp() {
        return gameStartTimestamp;
    }

    /**
     * Sets the game start timestamp.
     *
     * @param gameStartTimestamp Unix timestamp in milliseconds
     */
    public void setGameStartTimestamp(long gameStartTimestamp) {
        this.gameStartTimestamp = gameStartTimestamp;
    }

    /**
     * Gets the game type.
     *
     * @return The game type
     */
    public String getGameType() {
        return gameType;
    }

    /**
     * Sets the game type.
     *
     * @param gameType The game type
     */
    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    /**
     * Gets the game version.
     *
     * @return The game version string
     */
    public String getGameVersion() {
        return gameVersion;
    }

    /**
     * Sets the game version.
     *
     * @param gameVersion The game version string
     */
    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    /**
     * Gets the map ID.
     *
     * @return The map ID
     */
    public int getMapId() {
        return mapId;
    }

    /**
     * Sets the map ID.
     *
     * @param mapId The map ID
     */
    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    /**
     * Gets the list of participants.
     *
     * @return List of all participants in the game
     */
    public List<ParticipantDto> getParticipants() {
        return participants;
    }

    /**
     * Sets the list of participants.
     *
     * @param participants List of participants
     */
    public void setParticipants(List<ParticipantDto> participants) {
        this.participants = participants;
    }

    /**
     * Gets the platform ID.
     *
     * @return The platform/region ID
     */
    public String getPlatformId() {
        return platformId;
    }

    /**
     * Sets the platform ID.
     *
     * @param platformId The platform/region ID
     */
    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    /**
     * Gets the queue ID.
     *
     * @return The queue ID
     */
    public int getQueueId() {
        return queueId;
    }

    /**
     * Sets the queue ID.
     *
     * @param queueId The queue ID
     */
    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    /**
     * Gets the tournament code.
     *
     * @return The tournament code, or null if not a tournament game
     */
    public String getTournamentCode() {
        return tournamentCode;
    }

    /**
     * Sets the tournament code.
     *
     * @param tournamentCode The tournament code
     */
    public void setTournamentCode(String tournamentCode) {
        this.tournamentCode = tournamentCode;
    }

    /**
     * Gets the LP change for this game.
     * This is calculated by the application, not provided by Riot API.
     *
     * @return The LP change, or null if not calculated
     */
    public Integer getLpChange() {
        return lpChange;
    }

    /**
     * Sets the LP change for this game.
     *
     * @param lpChange The LP change value
     */
    public void setLpChange(Integer lpChange) {
        this.lpChange = lpChange;
    }
}
