-- Flyway baseline migration for SummonerAPI
-- Table: player_lp_records

CREATE TABLE IF NOT EXISTS player_lp_records (
    id BIGSERIAL PRIMARY KEY,
    puuid VARCHAR(128) NOT NULL,
    queue_type VARCHAR(32) NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    league_points INTEGER NOT NULL,
    tier VARCHAR(32),
    rank VARCHAR(8)
);

-- Composite index to speed up LP change lookups around match timestamps
CREATE INDEX IF NOT EXISTS idx_puuid_queuetype_timestamp
    ON player_lp_records (puuid, queue_type, timestamp DESC);
