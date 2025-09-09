-- Create table for JPA entity com.zerox80.riotapi.model.Summoner
-- Mirrors fields in Summoner.java and Spring/Hibernate default naming strategy (snake_case)

CREATE TABLE IF NOT EXISTS summoner (
    id               VARCHAR(128) PRIMARY KEY,
    account_id       VARCHAR(128),
    puuid            VARCHAR(128) NOT NULL,
    name             VARCHAR(64)  NOT NULL,
    profile_icon_id  INTEGER,
    revision_date    BIGINT,
    summoner_level   BIGINT       NOT NULL
);

-- Helpful indexes for lookup by puuid and name
CREATE INDEX IF NOT EXISTS idx_summoner_puuid ON summoner(puuid);
CREATE INDEX IF NOT EXISTS idx_summoner_name  ON summoner(name);
