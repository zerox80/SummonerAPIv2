package com.zerox80.riotapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data Transfer Object representing a League of Legends match.
 *
 * <p>This class is the top-level container for all data related to a single match.
 * It includes metadata about the match and detailed information about the game,
 * including participants, game mode, and timings.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchV5Dto {
    /** Metadata about the match. */
    private MetadataDto metadata;
    /** Detailed information about the game. */
    private InfoDto info;

    /**
     * Gets the metadata for the match.
     *
     * @return The match metadata
     */
    public MetadataDto getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata for the match.
     *
     * @param metadata The match metadata
     */
    public void setMetadata(MetadataDto metadata) {
        this.metadata = metadata;
    }

    /**
     * Gets the detailed information about the game.
     *
     * @return The game information
     */
    public InfoDto getInfo() {
        return info;
    }

    /**
     * Sets the detailed information about the game.
     *
     * @param info The game information
     */
    public void setInfo(InfoDto info) {
        this.info = info;
    }
}
