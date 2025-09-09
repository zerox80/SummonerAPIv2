-- Ensure sequence exists for Hibernate AUTO strategy (legacy) on player_lp_records
-- Safe even if unused when switching to IDENTITY; satisfies schema validation

CREATE SEQUENCE IF NOT EXISTS player_lp_records_seq START 1 INCREMENT 1;
