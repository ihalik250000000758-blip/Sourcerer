-- Inserts or refreshes the sample save from sourcerer_progress.properties and
-- sourcerer_tutorial.properties as one clearly labeled test profile.
-- Run after 02_create_tables.sql and 04_seed_game_data.sql.
-- Safe to rerun: player_name and each player-owned primary key prevent duplicates.
-- The local tutorialDone=true property is intentionally omitted: that one-time
-- introductory flag is transient and is not part of the MariaDB schema.
USE sourcerer_crusade;

START TRANSACTION;

INSERT INTO players (player_name, xp, character_level, coins)
VALUES ('Sourcerer Test Dummy', 125, 5, 175)
ON DUPLICATE KEY UPDATE
    xp = VALUES(xp),
    character_level = VALUES(character_level),
    coins = VALUES(coins);

SELECT player_id INTO @dummy_player_id
FROM players
WHERE player_name = 'Sourcerer Test Dummy';

INSERT INTO inventory (player_id, potion_code, quantity) VALUES
    (@dummy_player_id, 'HEALING', 10),
    (@dummy_player_id, 'MANA', 2)
ON DUPLICATE KEY UPDATE quantity = VALUES(quantity);

-- Seven completed levels at three stars each: 21 campaign stars total.
-- Every level is unlocked because all preceding levels are completed.
INSERT INTO player_progress
    (player_id, stage_id, unlocked, completed, stars, completed_at)
VALUES
    (@dummy_player_id, 1, TRUE, TRUE, 3, CURRENT_TIMESTAMP),
    (@dummy_player_id, 2, TRUE, TRUE, 3, CURRENT_TIMESTAMP),
    (@dummy_player_id, 3, TRUE, TRUE, 3, CURRENT_TIMESTAMP),
    (@dummy_player_id, 4, TRUE, TRUE, 3, CURRENT_TIMESTAMP),
    (@dummy_player_id, 5, TRUE, TRUE, 3, CURRENT_TIMESTAMP),
    (@dummy_player_id, 6, TRUE, TRUE, 3, CURRENT_TIMESTAMP),
    (@dummy_player_id, 7, TRUE, TRUE, 3, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
    unlocked = VALUES(unlocked),
    completed = VALUES(completed),
    stars = VALUES(stars),
    completed_at = VALUES(completed_at);

-- Six completed tutorial minigames at three stars each: 18 tutorial stars total.
INSERT INTO tutorial_progress
    (player_id, tutorial_stage_id, unlocked, completed, stars, completed_at)
VALUES
    (@dummy_player_id, 1, TRUE, TRUE, 3, CURRENT_TIMESTAMP),
    (@dummy_player_id, 2, TRUE, TRUE, 3, CURRENT_TIMESTAMP),
    (@dummy_player_id, 3, TRUE, TRUE, 3, CURRENT_TIMESTAMP),
    (@dummy_player_id, 4, TRUE, TRUE, 3, CURRENT_TIMESTAMP),
    (@dummy_player_id, 5, TRUE, TRUE, 3, CURRENT_TIMESTAMP),
    (@dummy_player_id, 6, TRUE, TRUE, 3, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
    unlocked = VALUES(unlocked),
    completed = VALUES(completed),
    stars = VALUES(stars),
    completed_at = VALUES(completed_at);

COMMIT;

-- Immediate confirmation of the inserted test profile and its requested totals.
SELECT p.player_id, p.player_name, p.character_level AS player_level, p.xp, p.coins,
       COALESCE((SELECT i.quantity FROM inventory i
                 WHERE i.player_id = p.player_id AND i.potion_code = 'HEALING'), 0) AS healing_potions,
       COALESCE((SELECT i.quantity FROM inventory i
                 WHERE i.player_id = p.player_id AND i.potion_code = 'MANA'), 0) AS mana_potions,
       COALESCE((SELECT SUM(pp.stars) FROM player_progress pp
                 WHERE pp.player_id = p.player_id), 0) AS campaign_stars,
       COALESCE((SELECT SUM(tp.stars) FROM tutorial_progress tp
                 WHERE tp.player_id = p.player_id), 0) AS tutorial_stars
FROM players p
WHERE p.player_name = 'Sourcerer Test Dummy';
