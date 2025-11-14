// Package declaration: Defines that this class belongs to the repository package
package com.zerox80.riotapi.repository;

// Import for the ChampionItemStat entity that this repository manages
import com.zerox80.riotapi.model.ChampionItemStat;
// Import for JPA repository base interface providing CRUD operations
import org.springframework.data.jpa.repository.JpaRepository;
// Import for Spring's repository stereotype annotation
import org.springframework.stereotype.Repository;

// Import for Java List collection
import java.util.List;
// Import for Optional to handle potentially null query results
import java.util.Optional;


/**
 * JPA Repository for ChampionItemStat entities.
 * Provides database access for champion item build statistics aggregated from high-ELO matches.
 *
 * This repository manages data about which items are most commonly built on each champion,
 * tracked per patch version, queue type, and role. Statistics are used to display recommended
 * item builds to players based on actual game data.
 *
 * Custom query methods follow Spring Data JPA naming conventions to find specific item builds
 * and top builds by usage count.
 */
@Repository
public interface ChampionItemStatRepository extends JpaRepository<ChampionItemStat, Long> {

    /**
     * Finds a specific item build record for a champion.
     * Used to check if a specific item build already exists before updating statistics.
     *
     * @param championId The champion identifier (e.g., "266" for Aatrox)
     * @param role The lane role (TOP, JUNGLE, MID, ADC, SUPPORT)
     * @param patch The patch version (e.g., "15.18")
     * @param queueId The queue type (420 for Solo/Duo, 440 for Flex)
     * @param itemId The specific item identifier
     * @return Optional containing the matching ChampionItemStat if found, empty otherwise
     */
    Optional<ChampionItemStat> findByChampionIdAndRoleAndPatchAndQueueIdAndItemId(String championId, String role, String patch, int queueId, int itemId);

    /**
     * Finds the top 10 most popular item builds for a champion in a specific role.
     * Results are ordered by count (descending) to show most frequently built items first.
     *
     * @param championId The champion identifier
     * @param role The lane role
     * @param patch The patch version
     * @param queueId The queue type
     * @return List of up to 10 item stats ordered by usage count (most popular first)
     */
    List<ChampionItemStat> findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(String championId, String role, String patch, int queueId);

    /**
     * Finds the top 10 most popular item builds for a champion across all roles.
     * Used when role-specific data is not needed or available.
     *
     * @param championId The champion identifier
     * @param patch The patch version
     * @param queueId The queue type
     * @return List of up to 10 item stats ordered by usage count (most popular first)
     */
    List<ChampionItemStat> findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(String championId, String patch, int queueId);

    /**
     * Deletes all item build records for a champion in a specific patch and queue.
     * Used when recalculating statistics or cleaning up outdated data.
     *
     * @param championId The champion identifier
     * @param patch The patch version
     * @param queueId The queue type
     */
    void deleteByChampionIdAndPatchAndQueueId(String championId, String patch, int queueId);
}
