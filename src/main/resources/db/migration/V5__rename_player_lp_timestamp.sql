-- Rename column 'timestamp' to 'lp_timestamp' to avoid reserved keyword conflicts
-- and align with updated JPA mapping in com.zerox80.riotapi.model.PlayerLpRecord

-- Drop old index if it exists (schema-aware, PostgreSQL)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE c.relkind = 'i'
          AND c.relname = 'idx_puuid_queuetype_timestamp'
          AND n.nspname = current_schema()
    ) THEN
        EXECUTE format('DROP INDEX %I.%I', current_schema(), 'idx_puuid_queuetype_timestamp');
    END IF;
END
$$;

-- Rename the column if it exists with the old name (schema-aware, PostgreSQL)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'player_lp_records'
          AND column_name = 'timestamp'
    ) THEN
        EXECUTE format('ALTER TABLE %I.%I RENAME COLUMN %I TO %I', current_schema(), 'player_lp_records', 'timestamp', 'lp_timestamp');
    END IF;
END
$$;

-- Create the new index on the renamed column (idempotent, schema-aware, PostgreSQL)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE c.relkind = 'i'
          AND c.relname = 'idx_puuid_queuetype_timestamp'
          AND n.nspname = current_schema()
    ) THEN
        EXECUTE format('CREATE INDEX %I.%I ON %I.%I (puuid, queue_type, lp_timestamp DESC)',
                       current_schema(), 'idx_puuid_queuetype_timestamp', current_schema(), 'player_lp_records');
    END IF;
END
$$;
