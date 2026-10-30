-- Marks campaign levels 1-6 and tutorial stages 1-5 as cleared (3 stars each),
-- and leaves campaign level 7 and tutorial stage 6 unlocked and ready to play.
-- Targets the profile the game actually loads: 'Sourcerer'.
-- Safe to rerun. Run after 02_create_tables.sql and 04_seed_game_data.sql.
USE sourcerer_crusade;

START TRANSACTION;

INSERT INTO players (player_name) VALUES ('Sourcerer')
ON DUPLICATE KEY UPDATE player_name = player_name;

SELECT player_id INTO @pid FROM players WHERE player_name = 'Sourcerer';

INSERT INTO player_progress (player_id, stage_id, unlocked, completed, stars, completed_at) VALUES
    (@pid, 1, TRUE, TRUE,  3, CURRENT_TIMESTAMP),
    (@pid, 2, TRUE, TRUE,  3, CURRENT_TIMESTAMP),
    (@pid, 3, TRUE, TRUE,  3, CURRENT_TIMESTAMP),
    (@pid, 4, TRUE, TRUE,  3, CURRENT_TIMESTAMP),
    (@pid, 5, TRUE, TRUE,  3, CURRENT_TIMESTAMP),
    (@pid, 6, TRUE, TRUE,  3, CURRENT_TIMESTAMP),
    (@pid, 7, TRUE, FALSE, 0, NULL)
ON DUPLICATE KEY UPDATE
    unlocked = VALUES(unlocked), completed = VALUES(completed),
    stars = VALUES(stars), completed_at = VALUES(completed_at);

INSERT INTO tutorial_progress (player_id, tutorial_stage_id, unlocked, completed, stars, completed_at) VALUES
    (@pid, 1, TRUE, TRUE,  3, CURRENT_TIMESTAMP),
    (@pid, 2, TRUE, TRUE,  3, CURRENT_TIMESTAMP),
    (@pid, 3, TRUE, TRUE,  3, CURRENT_TIMESTAMP),
    (@pid, 4, TRUE, TRUE,  3, CURRENT_TIMESTAMP),
    (@pid, 5, TRUE, TRUE,  3, CURRENT_TIMESTAMP),
    (@pid, 6, TRUE, FALSE, 0, NULL)
ON DUPLICATE KEY UPDATE
    unlocked = VALUES(unlocked), completed = VALUES(completed),
    stars = VALUES(stars), completed_at = VALUES(completed_at);

COMMIT;

SELECT 'campaign' AS kind, stage_id AS id, unlocked, completed, stars
FROM player_progress WHERE player_id = @pid
UNION ALL
SELECT 'tutorial', tutorial_stage_id, unlocked, completed, stars
FROM tutorial_progress WHERE player_id = @pid
ORDER BY kind, id;
