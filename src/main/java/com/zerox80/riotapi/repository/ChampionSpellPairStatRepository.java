package com.zerox80.riotapi.repository;

import com.zerox80.riotapi.model.ChampionSpellPairStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChampionSpellPairStatRepository extends JpaRepository<ChampionSpellPairStat, Long> {
    Optional<ChampionSpellPairStat> findByChampionIdAndRoleAndPatchAndQueueIdAndSpell1IdAndSpell2Id(
            String championId, String role, String patch, int queueId, int spell1Id, int spell2Id);
    List<ChampionSpellPairStat> findTop10ByChampionIdAndRoleAndPatchAndQueueIdOrderByCountDesc(String championId, String role, String patch, int queueId);
    List<ChampionSpellPairStat> findTop10ByChampionIdAndPatchAndQueueIdOrderByCountDesc(String championId, String patch, int queueId);
    void deleteByChampionIdAndPatchAndQueueId(String championId, String patch, int queueId);
}
