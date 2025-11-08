package com.zerox80.riotapi.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data Transfer Object representing metadata for a match.
 *
 * <p>This class encapsulates metadata about a specific League of Legends match,
 * including the data version, match ID, and a list of participant PUUIDs.
 * It is part of the larger MatchV5Dto structure.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataDto {
    /** The version of the data. */
    private String dataVersion;
    /** The unique identifier for the match. */
    private String matchId;
    /** A list of participant PUUIDs. */
    private List<String> participants;

    /**
     * Gets the version of the data.
     *
     * @return The data version
     */
    public String getDataVersion() {
        return dataVersion;
    }

    /**
     * Sets the version of the data.
     *
     * @param dataVersion The data version
     */
    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }

    /**
     * Gets the unique identifier for the match.
     *
     * @return The match ID
     */
    public String getMatchId() {
        return matchId;
    }

    /**
     * Sets the unique identifier for the match.
     *
     * @param matchId The match ID
     */
    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    /**
     * Gets the list of participant PUUIDs.
     *
     * @return The list of participants
     */
    public List<String> getParticipants() {
        return participants;
    }

    /**
     * Sets the list of participant PUUIDs.
     *
     * @param participants The list of participants
     */
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
