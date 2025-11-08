package com.zerox80.riotapi.repository;

import com.zerox80.riotapi.model.ChampionItemStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link ChampionItemStat} entities.
 *
 * <p>This repository provides CRUD operations for champion item statistics,
 * as well as custom query methods for finding and deleting item stats based on
 * various criteria such as champion, role, patch, and queue.</p>
 *
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Repository
public interface ChampionItemStatRepository extends JpaRepository<ChampionItemStat, Long> {
    /**
     * Finds a specific champion item statistic by its unique composite key.
     *
     * @param championId The champion's ID.
     * @param role The role.
     * @param patch The patch version.
     * @param queueId The queue ID.
     * @param itemId The item ID.
     * @return An {@link Optional} containing the found statistic, or empty if not found.
     */
    Optional<ChampionItemStat> findByChampionIdAndRoleAndPatchAndQueueIdAndItemId(String championId, String role, String patch, int queueId, int itemId);

    /**
     * Finds the top 10 most popular item statistics for a given champion, role, patch, and queue.
     *
     * @param championId The champion's ID.
     * @param role The role.
     * @param patch The patch version.
     * @param queueId The queue ID.
     * @return A list of the top 10 item statistics.
     */
    List<ChampionItemStat> findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(String championId, String role, String patch, int queueId);

    /**
     * Finds the top 10 most popular item statistics for a given champion, patch, and queue, across all roles.
     *
     * @param championId The champion's ID.
     * @param patch The patch version.
     * @param queueId The queue ID.
     * @return A list of the top 10 item statistics.
     */
    List<ChampionItemStat> findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(String championId, String patch, int queueId);

    /**
     * Deletes all item statistics for a given champion, patch, and queue.
     *
     * @param championId The champion's ID.
     * @param patch The patch version.
     * @param queueId The queue ID.
     */
    void deleteByChampionIdAndPatchAndQueueId(String championId, String patch, int queueId);
}
