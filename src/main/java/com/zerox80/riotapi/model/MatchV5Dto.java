// Package declaration: Defines that this class belongs to the model package
package com.zerox80.riotapi.model;

// Import for Jackson annotation to ignore unknown JSON properties from Riot API
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Data model representing a complete match from Riot's Match-V5 API.
 * Contains both match metadata and detailed match information.
 *
 * This model is the top-level response structure from the Match-V5 API.
 * It contains two main sections: metadata (match ID, participant PUUIDs)
 * and info (detailed game data, participants, stats). The JsonIgnoreProperties
 * annotation allows graceful handling of new fields added by Riot.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchV5Dto {

    // Match metadata including match ID and participant list
    private MetadataDto metadata;

    // Detailed match information including game stats and participant details
    private InfoDto info;

    /**
     * Gets the match metadata.
     *
     * @return The metadata object
     */
    public MetadataDto getMetadata() {
        return metadata;
    }

    /**
     * Sets the match metadata.
     *
     * @param metadata The metadata object
     */
    public void setMetadata(MetadataDto metadata) {
        this.metadata = metadata;
    }

    /**
     * Gets the detailed match information.
     *
     * @return The info object
     */
    public InfoDto getInfo() {
        return info;
    }

    /**
     * Sets the detailed match information.
     *
     * @param info The info object
     */
    public void setInfo(InfoDto info) {
        this.info = info;
    }
}
