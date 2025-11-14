// Package declaration: Defines that this class belongs to the repository package
package com.zerox80.riotapi.repository;

// Import for the ChampionSpellPairStat entity that this repository manages
import com.zerox80.riotapi.model.ChampionSpellPairStat;
// Import for JPA repository base interface providing CRUD operations
import org.springframework.data.jpa.repository.JpaRepository;
// Import for Spring's repository stereotype annotation
import org.springframework.stereotype.Repository;

// Import for Java List collection
import java.util.List;
// Import for Optional to handle potentially null query results
import java.util.Optional;


/**
 * JPA Repository for ChampionSpellPairStat entities.
 * Provides database access for champion summoner spell pair statistics aggregated from high-ELO matches.
 *
 * This repository manages data about which summoner spell combinations (e.g., Flash + Ignite,
 * Flash + Teleport) are most commonly used on each champion. Statistics are tracked per
 * patch version, queue type, and role to help players choose optimal summoner spells.
 *
 * Custom query methods follow Spring Data JPA naming conventions to find specific spell pairs
 * and top pairs by usage count.
 */
@Repository
public interface ChampionSpellPairStatRepository extends JpaRepository<ChampionSpellPairStat, Long> {

    /**
     * Finds a specific summoner spell pair record for a champion.
     * Used to check if a specific spell combination already exists before updating statistics.
     *
     * @param championId The champion identifier (e.g., "266" for Aatrox)
     * @param role The lane role (TOP, JUNGLE, MID, ADC, SUPPORT)
     * @param patch The patch version (e.g., "15.18")
     * @param queueId The queue type (420 for Solo/Duo, 440 for Flex)
     * @param spell1Id The first summoner spell ID (e.g., 4 for Flash)
     * @param spell2Id The second summoner spell ID (e.g., 14 for Ignite)
     * @return Optional containing the matching ChampionSpellPairStat if found, empty otherwise
     */
    Optional<ChampionSpellPairStat> findByChampionIdAndRoleAndPatchAndQueueIdAndSpell1IdAndSpell2Id(
            String championId, String role, String patch, int queueId, int spell1Id, int spell2Id);

    /**
     * Finds the top 10 most popular summoner spell pairs for a champion in a specific role.
     * Results are ordered by count (descending) to show most frequently used spell pairs first.
     *
     * @param championId The champion identifier
     * @param role The lane role
     * @param patch The patch version
     * @param queueId The queue type
     * @return List of up to 10 spell pair stats ordered by usage count (most popular first)
     */
    List<ChampionSpellPairStat> findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(String championId, String role, String patch, int queueId);

    /**
     * Finds the top 10 most popular summoner spell pairs for a champion across all roles.
     * Used when role-specific data is not needed or available.
     *
     * @param championId The champion identifier
     * @param patch The patch version
     * @param queueId The queue type
     * @return List of up to 10 spell pair stats ordered by usage count (most popular first)
     */
    List<ChampionSpellPairStat> findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(String championId, String patch, int queueId);

    /**
     * Deletes all summoner spell pair records for a champion in a specific patch and queue.
     * Used when recalculating statistics or cleaning up outdated data.
     *
     * @param championId The champion identifier
     * @param patch The patch version
     * @param queueId The queue type
     */
    void deleteByChampionIdAndPatchAndQueueId(String championId, String patch, int queueId);
}
