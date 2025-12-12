// Package declaration: Defines that this class belongs to the repository package
package com.zerox80.riotapi.repository;

// Import for the ChampionRuneStat entity that this repository manages
import com.zerox80.riotapi.model.ChampionRuneStat;
// Import for JPA repository base interface providing CRUD operations
import org.springframework.data.jpa.repository.JpaRepository;
// Import for Spring's repository stereotype annotation
import org.springframework.stereotype.Repository;

// Import for Java List collection
import java.util.List;
// Import for Optional to handle potentially null query results
import java.util.Optional;

/**
 * JPA Repository for ChampionRuneStat entities.
 * Provides database access for champion rune configuration statistics
 * aggregated from high-ELO matches.
 *
 * This repository manages data about which rune combinations are most commonly
 * used on each champion.
 * Rune data includes the primary rune tree, secondary rune tree, and keystone
 * rune, tracked per
 * patch version, queue type, and role. Statistics help players choose optimal
 * rune setups.
 *
 * Custom query methods follow Spring Data JPA naming conventions to find
 * specific rune configurations
 * and top configurations by usage count.
 */
@Repository
public interface ChampionRuneStatRepository extends JpaRepository<ChampionRuneStat, Long> {

    /**
     * Finds a specific rune configuration record for a champion.
     * Used to check if a specific rune setup already exists before updating
     * statistics.
     *
     * @param championId   The champion identifier (e.g., "266" for Aatrox)
     * @param role         The lane role (TOP, JUNGLE, MID, ADC, SUPPORT)
     * @param patch        The patch version (e.g., "15.18")
     * @param queueId      The queue type (420 for Solo/Duo, 440 for Flex)
     * @param primaryStyle The primary rune tree ID (e.g., 8000 for Precision)
     * @param subStyle     The secondary rune tree ID
     * @param keystone     The keystone rune ID (e.g., 8010 for Conqueror)
     * @return Optional containing the matching ChampionRuneStat if found, empty
     *         otherwise
     */
    Optional<ChampionRuneStat> findByChampionIdAndRoleAndPatchAndQueueIdAndPrimaryStyleAndSubStyleAndKeystone(
            String championId, String role, String patch, int queueId, int primaryStyle, int subStyle, int keystone);

    /**
     * Finds the top 10 most popular rune configurations for a champion in a
     * specific role.
     * Results are ordered by count (descending) to show most frequently used runes
     * first.
     *
     * @param championId The champion identifier
     * @param role       The lane role
     * @param patch      The patch version
     * @param queueId    The queue type
     * @return List of up to 10 rune stats ordered by usage count (most popular
     *         first)
     */
    List<ChampionRuneStat> findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(String championId,
            String role, String patch, int queueId);

    /**
     * Finds the top 10 most popular rune configurations for a champion across all
     * roles.
     * Used when role-specific data is not needed or available.
     *
     * @param championId The champion identifier
     * @param patch      The patch version
     * @param queueId    The queue type
     * @return List of up to 10 rune stats ordered by usage count (most popular
     *         first)
     */
    List<ChampionRuneStat> findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(String championId, String patch,
            int queueId);

    /**
     * Deletes all rune configuration records for a champion in a specific patch and
     * queue.
     * Used when recalculating statistics or cleaning up outdated data.
     *
     * @param championId The champion identifier
     * @param patch      The patch version
     * @param queueId    The queue type
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM ChampionRuneStat c WHERE c.championId = :championId AND c.patch = :patch AND c.queueId = :queueId")
    void deleteByChampionIdAndPatchAndQueueId(String championId, String patch, int queueId);
}
