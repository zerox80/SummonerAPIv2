package com.zerox80.riotapi.repository;

import com.zerox80.riotapi.model.PlayerLpRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerLpRecordRepository extends JpaRepository<PlayerLpRecord, Long> {

    Optional<PlayerLpRecord> findFirstByPuuidAndQueueTypeAndTimestampBeforeOrderByTimestampDesc(
            String puuid, String queueType, Instant timestamp);

    Optional<PlayerLpRecord> findFirstByPuuidAndQueueTypeAndTimestampGreaterThanEqualOrderByTimestampAsc(
            String puuid, String queueType, Instant timestamp);

    List<PlayerLpRecord> findByPuuidAndQueueTypeOrderByTimestampDesc(String puuid, String queueType);

    List<PlayerLpRecord> findByPuuid(String puuid);
} 