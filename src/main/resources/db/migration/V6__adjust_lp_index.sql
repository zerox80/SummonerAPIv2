-- Adjust player_lp_records composite index to remove DESC order for compatibility with JPA/Hibernate
-- Safe re-creation: drop existing index (if present) and recreate without DESC

DROP INDEX IF EXISTS idx_puuid_queuetype_timestamp;
CREATE INDEX idx_puuid_queuetype_timestamp
    ON player_lp_records (puuid, queue_type, lp_timestamp);
