package com.zerox80.riotapi.repository;

import com.zerox80.riotapi.model.ChampionRuneStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChampionRuneStatRepository extends JpaRepository<ChampionRuneStat, Long> {
    Optional<ChampionRuneStat> findByChampionIdAndRoleAndPatchAndQueueIdAndPrimaryStyleAndSubStyleAndKeystone(
            String championId, String role, String patch, int queueId, int primaryStyle, int subStyle, int keystone);
    List<ChampionRuneStat> findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(String championId, String role, String patch, int queueId);
    List<ChampionRuneStat> findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(String championId, String patch, int queueId);
    void deleteByChampionIdAndPatchAndQueueId(String championId, String patch, int queueId);
}
