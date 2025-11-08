package com.zerox80.riotapi.repository;

import com.zerox80.riotapi.model.PlayerLpRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link PlayerLpRecord} entities.
 *
 * <p>This repository provides CRUD operations for player LP records, as well as
 * custom query methods for finding LP records based on various criteria such as
 * PUUID, queue type, and timestamp.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Repository
public interface PlayerLpRecordRepository extends JpaRepository<PlayerLpRecord, Long> {

    /**
     * Finds the most recent LP record for a player in a specific queue before a given timestamp.
     *
     * @param puuid The player's PUUID.
     * @param queueType The queue type.
     * @param timestamp The timestamp to search before.
     * @return An {@link Optional} containing the found LP record, or empty if not found.
     */
    Optional<PlayerLpRecord> findFirstByPuuidAndQueueTypeAndTimestampBeforeOrderByTimestampDesc(
            String puuid, String queueType, Instant timestamp);

    /**
     * Finds the earliest LP record for a player in a specific queue at or after a given timestamp.
     *
     * @param puuid The player's PUUID.
     * @param queueType The queue type.
     * @param timestamp The timestamp to search at or after.
     * @return An {@link Optional} containing the found LP record, or empty if not found.
     */
    Optional<PlayerLpRecord> findFirstByPuuidAndQueueTypeAndTimestampGreaterThanEqualOrderByTimestampAsc(
            String puuid, String queueType, Instant timestamp);

    /**
     * Finds all LP records for a player in a specific queue, ordered by timestamp descending.
     *
     * @param puuid The player's PUUID.
     * @param queueType The queue type.
     * @return A list of all LP records for the player in the specified queue.
     */
    List<PlayerLpRecord> findByPuuidAndQueueTypeOrderByTimestampDesc(String puuid, String queueType);

    /**
     * Finds all LP records for a player across all queues.
     *
     * @param puuid The player's PUUID.
     * @return A list of all LP records for the player.
     */
    List<PlayerLpRecord> findByPuuid(String puuid);
}
