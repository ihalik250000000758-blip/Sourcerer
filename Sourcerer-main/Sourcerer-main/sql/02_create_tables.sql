-- Fresh-install schema for the current Java game (7 campaign levels, 6 tutorial minigames).
-- Run after 01_create_database.sql. All player-owned rows cascade when a test profile is removed.
USE sourcerer_crusade;

CREATE TABLE IF NOT EXISTS players (
    player_id BIGINT NOT NULL AUTO_INCREMENT,
    player_name VARCHAR(80) NOT NULL,
    xp INT NOT NULL DEFAULT 0,
    character_level INT NOT NULL DEFAULT 1,
    coins INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (player_id),
    UNIQUE KEY uq_players_name (player_name),
    CONSTRAINT chk_players_xp CHECK (xp >= 0),
    CONSTRAINT chk_players_level CHECK (character_level >= 1),
    CONSTRAINT chk_players_coins CHECK (coins >= 0),
    CONSTRAINT chk_players_xp_range CHECK (xp < 50 + 25 * (character_level - 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS stages (
    stage_id TINYINT UNSIGNED NOT NULL,
    stage_name VARCHAR(120) NOT NULL,
    description VARCHAR(500) NOT NULL DEFAULT '',
    PRIMARY KEY (stage_id),
    CONSTRAINT chk_stages_id CHECK (stage_id BETWEEN 1 AND 7)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS player_progress (
    player_id BIGINT NOT NULL,
    stage_id TINYINT UNSIGNED NOT NULL,
    unlocked BOOLEAN NOT NULL DEFAULT FALSE,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    stars TINYINT UNSIGNED NOT NULL DEFAULT 0,
    completed_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (player_id, stage_id),
    KEY ix_player_progress_stage (stage_id),
    CONSTRAINT fk_progress_player FOREIGN KEY (player_id) REFERENCES players (player_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_progress_stage FOREIGN KEY (stage_id) REFERENCES stages (stage_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_progress_stars CHECK (stars BETWEEN 0 AND 3),
    CONSTRAINT chk_progress_stars_match CHECK (completed = (stars > 0)),
    CONSTRAINT chk_progress_completed_at CHECK (completed = (completed_at IS NOT NULL)),
    CONSTRAINT chk_progress_unlock_order CHECK (unlocked OR NOT completed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tutorial minigame stars are separate from the one-time introductory tutorial flag,
-- which is deliberately session-only and is not stored in this schema.
CREATE TABLE IF NOT EXISTS tutorial_stages (
    tutorial_stage_id TINYINT UNSIGNED NOT NULL,
    title VARCHAR(80) NOT NULL,
    mechanic VARCHAR(120) NOT NULL DEFAULT '',
    PRIMARY KEY (tutorial_stage_id),
    CONSTRAINT chk_tutorial_stage_id CHECK (tutorial_stage_id BETWEEN 1 AND 6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tutorial_progress (
    player_id BIGINT NOT NULL,
    tutorial_stage_id TINYINT UNSIGNED NOT NULL,
    unlocked BOOLEAN NOT NULL DEFAULT FALSE,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    stars TINYINT UNSIGNED NOT NULL DEFAULT 0,
    completed_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (player_id, tutorial_stage_id),
    KEY ix_tutorial_progress_stage (tutorial_stage_id),
    CONSTRAINT fk_tutorial_player FOREIGN KEY (player_id) REFERENCES players (player_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_tutorial_stage FOREIGN KEY (tutorial_stage_id) REFERENCES tutorial_stages (tutorial_stage_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_tutorial_stars CHECK (stars BETWEEN 0 AND 3),
    CONSTRAINT chk_tutorial_stars_match CHECK (completed = (stars > 0)),
    CONSTRAINT chk_tutorial_completed_at CHECK (completed = (completed_at IS NOT NULL)),
    CONSTRAINT chk_tutorial_unlock_order CHECK (unlocked OR NOT completed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Potion codes match the Java enum. Missing rows mean quantity zero; new saves start empty.
CREATE TABLE IF NOT EXISTS inventory (
    player_id BIGINT NOT NULL,
    potion_code VARCHAR(40) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    PRIMARY KEY (player_id, potion_code),
    CONSTRAINT fk_inventory_player FOREIGN KEY (player_id) REFERENCES players (player_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_inventory_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_inventory_potion_code CHECK (potion_code IN ('HEALING', 'MANA'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
