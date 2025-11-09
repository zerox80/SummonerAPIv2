package com.zerox80.riotapi.repository;

import com.zerox80.riotapi.model.ChampionItemStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChampionItemStatRepository extends JpaRepository<ChampionItemStat, Long> {
    
    Optional<ChampionItemStat> findByChampionIdAndRoleAndPatchAndQueueIdAndItemId(String championId, String role, String patch, int queueId, int itemId);

    
    List<ChampionItemStat> findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(String championId, String role, String patch, int queueId);

    
    List<ChampionItemStat> findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(String championId, String patch, int queueId);

    
    void deleteByChampionIdAndPatchAndQueueId(String championId, String patch, int queueId);
}
