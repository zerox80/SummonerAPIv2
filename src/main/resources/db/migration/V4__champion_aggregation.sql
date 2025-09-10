-- Aggregation tables for champion builds (items, runes, summoner spells)

CREATE TABLE IF NOT EXISTS champion_item_stats (
    id           BIGSERIAL PRIMARY KEY,
    champion_id  VARCHAR(64)  NOT NULL,
    role         VARCHAR(16)  NOT NULL,
    patch        VARCHAR(16)  NOT NULL,
    queue_id     INTEGER      NOT NULL,
    item_id      INTEGER      NOT NULL,
    cnt          INTEGER      NOT NULL,
    wins         INTEGER      NOT NULL,
    CONSTRAINT uk_champion_item UNIQUE (champion_id, role, patch, queue_id, item_id)
);
CREATE INDEX IF NOT EXISTS idx_item_champ_patch ON champion_item_stats (champion_id, patch);
CREATE INDEX IF NOT EXISTS idx_item_champ_role_patch ON champion_item_stats (champion_id, role, patch);

CREATE TABLE IF NOT EXISTS champion_rune_stats (
    id             BIGSERIAL PRIMARY KEY,
    champion_id    VARCHAR(64)  NOT NULL,
    role           VARCHAR(16)  NOT NULL,
    patch          VARCHAR(16)  NOT NULL,
    queue_id       INTEGER      NOT NULL,
    primary_style  INTEGER      NOT NULL,
    sub_style      INTEGER      NOT NULL,
    keystone       INTEGER      NOT NULL,
    cnt            INTEGER      NOT NULL,
    wins           INTEGER      NOT NULL,
    CONSTRAINT uk_champion_rune UNIQUE (champion_id, role, patch, queue_id, primary_style, sub_style, keystone)
);
CREATE INDEX IF NOT EXISTS idx_rune_champ_patch ON champion_rune_stats (champion_id, patch);

CREATE TABLE IF NOT EXISTS champion_spell_pair_stats (
    id           BIGSERIAL PRIMARY KEY,
    champion_id  VARCHAR(64) NOT NULL,
    role         VARCHAR(16) NOT NULL,
    patch        VARCHAR(16) NOT NULL,
    queue_id     INTEGER     NOT NULL,
    spell1_id    INTEGER     NOT NULL,
    spell2_id    INTEGER     NOT NULL,
    cnt          INTEGER     NOT NULL,
    wins         INTEGER     NOT NULL,
    CONSTRAINT uk_champion_spell UNIQUE (champion_id, role, patch, queue_id, spell1_id, spell2_id)
);
CREATE INDEX IF NOT EXISTS idx_spell_champ_patch ON champion_spell_pair_stats (champion_id, patch);
