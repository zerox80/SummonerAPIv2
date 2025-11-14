// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Java List collection to hold participant PUUIDs
import java.util.List;
// Import for Jackson annotation to ignore unknown JSON properties from Riot API
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Data model representing match metadata from Riot's Match-V5 API.
 * Contains basic identifiers and participant list for a match.
 *
 * This model maps to the "metadata" section of the Match-V5 API response.
 * It provides the match ID (used to fetch match details) and a list of
 * all participant PUUIDs in the match. The JsonIgnoreProperties annotation
 * allows graceful handling of new fields added by Riot.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataDto {

    // The data version/format version for this match data
    private String dataVersion;

    // The unique match identifier (e.g., "NA1_1234567890")
    private String matchId;

    // List of PUUIDs for all 10 participants in the match
    private List<String> participants;

    /**
     * Gets the data version.
     *
     * @return The data version string
     */
    public String getDataVersion() {
        return dataVersion;
    }

    /**
     * Sets the data version.
     *
     * @param dataVersion The data version string
     */
    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }

    /**
     * Gets the match ID.
     *
     * @return The match ID
     */
    public String getMatchId() {
        return matchId;
    }

    /**
     * Sets the match ID.
     *
     * @param matchId The match ID
     */
    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    /**
     * Gets the list of participant PUUIDs.
     *
     * @return List of PUUIDs
     */
    public List<String> getParticipants() {
        return participants;
    }

    /**
     * Sets the list of participant PUUIDs.
     *
     * @param participants List of PUUIDs
     */
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
