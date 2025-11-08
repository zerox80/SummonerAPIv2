package com.zerox80.riotapi.repository;

import com.zerox80.riotapi.model.ChampionSpellPairStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link ChampionSpellPairStat} entities.
 *
 * <p>This repository provides CRUD operations for champion summoner spell pair statistics,
 * as well as custom query methods for finding and deleting spell pair stats based on
 * various criteria such as champion, role, patch, and queue.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Repository
public interface ChampionSpellPairStatRepository extends JpaRepository<ChampionSpellPairStat, Long> {
    /**
     * Finds a specific champion spell pair statistic by its unique composite key.
     *
     * @param championId The champion's ID.
     * @param role The role.
     * @param patch The patch version.
     * @param queueId The queue ID.
     * @param spell1Id The ID of the first summoner spell.
     * @param spell2Id The ID of the second summoner spell.
     * @return An {@link Optional} containing the found statistic, or empty if not found.
     */
    Optional<ChampionSpellPairStat> findByChampionIdAndRoleAndPatchAndQueueIdAndSpell1IdAndSpell2Id(
            String championId, String role, String patch, int queueId, int spell1Id, int spell2Id);

    /**
     * Finds the top 10 most popular spell pair statistics for a given champion, role, patch, and queue.
     *
     * @param championId The champion's ID.
     * @param role The role.
     * @param patch The patch version.
     * @param queueId The queue ID.
     * @return A list of the top 10 spell pair statistics.
     */
    List<ChampionSpellPairStat> findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(String championId, String role, String patch, int queueId);

    /**
     * Finds the top 10 most popular spell pair statistics for a given champion, patch, and queue, across all roles.
     *
     * @param championId The champion's ID.
     * @param patch The patch version.
     * @param queueId The queue ID.
     * @return A list of the top 10 spell pair statistics.
     */
    List<ChampionSpellPairStat> findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(String championId, String patch, int queueId);

    /**
     * Deletes all spell pair statistics for a given champion, patch, and queue.
     *
     * @param championId The champion's ID.
     * @param patch The patch version.
     * @param queueId The queue ID.
     */
    void deleteByChampionIdAndPatchAndQueueId(String championId, String patch, int queueId);
}
