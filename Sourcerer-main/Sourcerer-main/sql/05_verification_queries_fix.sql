-- Replacement for the first SELECT in 05_verification_queries.sql.
-- Fix: current_level now matches Progress.currentLevel() even when a player has no
-- player_progress rows yet (the old query returned the LAST stage instead of stage 1).
USE sourcerer_crusade;

SELECT p.player_id, p.player_name,
       COALESCE((SELECT MIN(s.stage_id)
                 FROM stages s
                 LEFT JOIN player_progress pp
                   ON pp.stage_id = s.stage_id AND pp.player_id = p.player_id
                 WHERE COALESCE(pp.completed, FALSE) = FALSE),
                (SELECT MAX(s2.stage_id) FROM stages s2)) AS current_level,
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
