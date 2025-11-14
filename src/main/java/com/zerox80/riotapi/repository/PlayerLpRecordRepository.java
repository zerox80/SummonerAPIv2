// Package declaration: Defines that this class belongs to the repository package
package com.zerox80.riotapi.repository;

// Import for the PlayerLpRecord entity that this repository manages
import com.zerox80.riotapi.model.PlayerLpRecord;
// Import for JPA repository base interface providing CRUD operations
import org.springframework.data.jpa.repository.JpaRepository;
// Import for Spring's repository stereotype annotation
import org.springframework.stereotype.Repository;

// Import for Java Instant to handle timestamp-based queries
import java.time.Instant;
// Import for Java List collection
import java.util.List;
// Import for Optional to handle potentially null query results
import java.util.Optional;


/**
 * JPA Repository for PlayerLpRecord entities.
 * Provides database access for player League Points (LP) history records.
 *
 * This repository tracks the historical LP (League Points) and rank changes for players
 * over time. Each record captures a player's rank, tier, LP, wins, and losses at a specific
 * point in time, allowing the application to calculate LP gains/losses between matches and
 * display rank progression.
 *
 * Custom query methods use timestamps to find LP records before or after specific matches,
 * enabling accurate calculation of LP changes per game.
 */
@Repository
public interface PlayerLpRecordRepository extends JpaRepository<PlayerLpRecord, Long> {

    /**
     * Finds the most recent LP record for a player before a specific timestamp.
     * Used to determine a player's rank and LP before a match started.
     *
     * @param puuid The player's unique identifier (PUUID)
     * @param queueType The ranked queue type (RANKED_SOLO_5x5 or RANKED_FLEX_SR)
     * @param timestamp The reference timestamp (typically a match start time)
     * @return Optional containing the most recent LP record before the timestamp, empty if none found
     */
    Optional<PlayerLpRecord> findFirstByPuuidAndQueueTypeAndTimestampBeforeOrderByTimestampDesc(
            String puuid, String queueType, Instant timestamp);

    /**
     * Finds the earliest LP record for a player at or after a specific timestamp.
     * Used to determine a player's rank and LP after a match ended.
     *
     * @param puuid The player's unique identifier (PUUID)
     * @param queueType The ranked queue type (RANKED_SOLO_5x5 or RANKED_FLEX_SR)
     * @param timestamp The reference timestamp (typically a match end time)
     * @return Optional containing the earliest LP record at or after the timestamp, empty if none found
     */
    Optional<PlayerLpRecord> findFirstByPuuidAndQueueTypeAndTimestampGreaterThanEqualOrderByTimestampAsc(
            String puuid, String queueType, Instant timestamp);

    /**
     * Finds all LP records for a player in a specific queue, ordered newest first.
     * Used to display a player's complete rank history for a queue type.
     *
     * @param puuid The player's unique identifier (PUUID)
     * @param queueType The ranked queue type (RANKED_SOLO_5x5 or RANKED_FLEX_SR)
     * @return List of all LP records for the player in the queue, ordered by timestamp descending
     */
    List<PlayerLpRecord> findByPuuidAndQueueTypeOrderByTimestampDesc(String puuid, String queueType);

    /**
     * Finds all LP records for a player across all queue types.
     * Used for comprehensive player statistics or cleanup operations.
     *
     * @param puuid The player's unique identifier (PUUID)
     * @return List of all LP records for the player across all queues
     */
    List<PlayerLpRecord> findByPuuid(String puuid);
}
