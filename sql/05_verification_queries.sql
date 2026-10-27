USE sourcerer_crusade;

-- Overall player view. Current campaign level is derived by Progress.currentLevel().
-- A player with no saved inventory or progress still appears with zero quantities/stars.
SELECT p.player_id, p.player_name,
       COALESCE((SELECT MIN(pp.stage_id) FROM player_progress pp
                 WHERE pp.player_id = p.player_id AND pp.completed = FALSE),
                (SELECT MAX(s.stage_id) FROM stages s)) AS current_level,
       p.character_level AS player_level, p.xp,
       50 + 25 * (p.character_level - 1) AS xp_to_next_level,
       p.coins,
       COALESCE((SELECT i.quantity FROM inventory i WHERE i.player_id = p.player_id
                 AND i.potion_code = 'HEALING'), 0) AS healing_potions,
       COALESCE((SELECT i.quantity FROM inventory i WHERE i.player_id = p.player_id
                 AND i.potion_code = 'MANA'), 0) AS mana_potions,
       COALESCE((SELECT SUM(pp.stars) FROM player_progress pp WHERE pp.player_id = p.player_id), 0)
           AS campaign_stars_out_of_21,
       COALESCE((SELECT SUM(pp.completed) FROM player_progress pp WHERE pp.player_id = p.player_id), 0)
           AS campaign_levels_completed,
       COALESCE((SELECT SUM(tp.stars) FROM tutorial_progress tp WHERE tp.player_id = p.player_id), 0)
           AS tutorial_minigame_stars_out_of_18,
       COALESCE((SELECT SUM(tp.completed) FROM tutorial_progress tp WHERE tp.player_id = p.player_id), 0)
           AS tutorial_minigames_completed
FROM players p
ORDER BY p.player_id;

-- Main levels and each player's completion, unlock state and stars.
SELECT stage_id, stage_name, description FROM stages ORDER BY stage_id;
SELECT s.stage_id, s.stage_name, p.player_name,
       COALESCE(pp.unlocked, s.stage_id = 1) AS unlocked,
       COALESCE(pp.completed, FALSE) AS completed,
       COALESCE(pp.stars, 0) AS stars, pp.completed_at
FROM stages s
CROSS JOIN players p
LEFT JOIN player_progress pp ON pp.player_id = p.player_id AND pp.stage_id = s.stage_id
ORDER BY p.player_name, s.stage_id;

-- Tutorial minigame stars, separate from the non-persistent first-run tutorial flag.
SELECT tutorial_stage_id, title, mechanic FROM tutorial_stages ORDER BY tutorial_stage_id;
SELECT ts.tutorial_stage_id, ts.title, p.player_name,
       COALESCE(tp.unlocked, ts.tutorial_stage_id = 1) AS unlocked,
       COALESCE(tp.completed, FALSE) AS completed,
       COALESCE(tp.stars, 0) AS stars, tp.completed_at
FROM tutorial_stages ts
CROSS JOIN players p
LEFT JOIN tutorial_progress tp
  ON tp.player_id = p.player_id AND tp.tutorial_stage_id = ts.tutorial_stage_id
ORDER BY p.player_name, ts.tutorial_stage_id;

-- Potion inventory. Empty inventory is represented as zero rows until a save writes quantities.
SELECT p.player_name, i.potion_code, i.quantity
FROM players p
LEFT JOIN inventory i ON i.player_id = p.player_id
ORDER BY p.player_name, i.potion_code;

-- This build has no campaign stages marked as bosses in LevelBank; there is no boss-defeat data.
SELECT 'No boss encounters are defined in the current seven-level campaign.' AS boss_progress;

-- List all tables in this schema.
SELECT TABLE_NAME
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;

-- Inspect columns, types, nullability, keys and defaults for all tables.
SELECT TABLE_NAME, ORDINAL_POSITION, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE,
       COLUMN_KEY, COLUMN_DEFAULT, EXTRA
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
ORDER BY TABLE_NAME, ORDINAL_POSITION;
