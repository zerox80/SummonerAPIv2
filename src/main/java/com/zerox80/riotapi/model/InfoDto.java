package com.zerox80.riotapi.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data Transfer Object representing game information from a match.
 *
 * <p>This class encapsulates metadata about a specific League of Legends match,
 * including game timings, mode, version, and participant details. It is part of
 * the larger MatchV5Dto structure and provides context for the match data.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoDto {
    /** The timestamp of when the game was created on the game server. */
    private long gameCreation;
    /** The duration of the game in seconds. */
    private long gameDuration;
    /** The timestamp of when the game ended. */
    private long gameEndTimestamp;
    /** The unique identifier for the game. */
    private long gameId;
    /** The game mode (e.g., CLASSIC, ARAM). */
    private String gameMode;
    /** The name of the game (custom games). */
    private String gameName;
    /** The timestamp of when the game started. */
    private long gameStartTimestamp;
    /** The type of game (e.g., MATCHED_GAME, CUSTOM_GAME). */
    private String gameType;
    /** The game version (patch) the match was played on. */
    private String gameVersion;
    /** The ID of the map the game was played on. */
    private int mapId;
    /** A list of all participants in the game. */
    private List<ParticipantDto> participants;
    /** The platform (region) where the game was played. */
    private String platformId;
    /** The queue ID for the game mode. */
    private int queueId;
    /** The tournament code if the game was part of a tournament. */
    private String tournamentCode;
    /** The change in League Points (LP) for the player in this match. */
    private Integer lpChange;

    /**
     * Gets the timestamp of when the game was created.
     *
     * @return The game creation timestamp
     */
    public long getGameCreation() {
        return gameCreation;
    }

    /**
     * Sets the timestamp of when the game was created.
     *
     * @param gameCreation The game creation timestamp
     */
    public void setGameCreation(long gameCreation) {
        this.gameCreation = gameCreation;
    }

    /**
     * Gets the duration of the game in seconds.
     *
     * @return The game duration
     */
    public long getGameDuration() {
        return gameDuration;
    }

    /**
     * Sets the duration of the game in seconds.
     *
     * @param gameDuration The game duration
     */
    public void setGameDuration(long gameDuration) {
        this.gameDuration = gameDuration;
    }

    /**
     * Gets the timestamp of when the game ended.
     *
     * @return The game end timestamp
     */
    public long getGameEndTimestamp() {
        return gameEndTimestamp;
    }

    /**
     * Sets the timestamp of when the game ended.
     *
     * @param gameEndTimestamp The game end timestamp
     */
    public void setGameEndTimestamp(long gameEndTimestamp) {
        this.gameEndTimestamp = gameEndTimestamp;
    }

    /**
     * Gets the unique identifier for the game.
     *
     * @return The game ID
     */
    public long getGameId() {
        return gameId;
    }

    /**
     * Sets the unique identifier for the game.
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
     * Gets the name of the game.
     *
     * @return The game name
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Sets the name of the game.
     *
     * @param gameName The game name
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Gets the timestamp of when the game started.
     *
     * @return The game start timestamp
     */
    public long getGameStartTimestamp() {
        return gameStartTimestamp;
    }

    /**
     * Sets the timestamp of when the game started.
     *
     * @param gameStartTimestamp The game start timestamp
     */
    public void setGameStartTimestamp(long gameStartTimestamp) {
        this.gameStartTimestamp = gameStartTimestamp;
    }

    /**
     * Gets the type of game.
     *
     * @return The game type
     */
    public String getGameType() {
        return gameType;
    }

    /**
     * Sets the type of game.
     *
     * @param gameType The game type
     */
    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    /**
     * Gets the game version.
     *
     * @return The game version
     */
    public String getGameVersion() {
        return gameVersion;
    }

    /**
     * Sets the game version.
     *
     * @param gameVersion The game version
     */
    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    /**
     * Gets the ID of the map.
     *
     * @return The map ID
     */
    public int getMapId() {
        return mapId;
    }

    /**
     * Sets the ID of the map.
     *
     * @param mapId The map ID
     */
    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    /**
     * Gets the list of participants in the game.
     *
     * @return The list of participants
     */
    public List<ParticipantDto> getParticipants() {
        return participants;
    }

    /**
     * Sets the list of participants in the game.
     *
     * @param participants The list of participants
     */
    public void setParticipants(List<ParticipantDto> participants) {
        this.participants = participants;
    }

    /**
     * Gets the platform (region) where the game was played.
     *
     * @return The platform ID
     */
    public String getPlatformId() {
        return platformId;
    }

    /**
     * Sets the platform (region) where the game was played.
     *
     * @param platformId The platform ID
     */
    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    /**
     * Gets the queue ID for the game mode.
     *
     * @return The queue ID
     */
    public int getQueueId() {
        return queueId;
    }

    /**
     * Sets the queue ID for the game mode.
     *
     * @param queueId The queue ID
     */
    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    /**
     * Gets the tournament code.
     *
     * @return The tournament code
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
     * Gets the change in League Points (LP).
     *
     * @return The LP change
     */
    public Integer getLpChange() {
        return lpChange;
    }

    /**
     * Sets the change in League Points (LP).
     *
     * @param lpChange The LP change
     */
    public void setLpChange(Integer lpChange) {
        this.lpChange = lpChange;
    }
}
