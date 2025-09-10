-- Rename column 'timestamp' to 'lp_timestamp' to avoid reserved keyword conflicts
-- and align with updated JPA mapping in com.zerox80.riotapi.model.PlayerLpRecord

-- Drop old index if it exists (works for PostgreSQL and H2 in PostgreSQL mode)
DROP INDEX IF EXISTS idx_puuid_queuetype_timestamp;

-- Rename the column if it exists with the old name
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'player_lp_records'
          AND column_name = 'timestamp'
    ) THEN
        EXECUTE 'ALTER TABLE player_lp_records RENAME COLUMN timestamp TO lp_timestamp';
    END IF;
END $$;

-- Create the new index on the renamed column (idempotent)
CREATE INDEX IF NOT EXISTS idx_puuid_queuetype_timestamp
    ON player_lp_records (puuid, queue_type, lp_timestamp DESC);
