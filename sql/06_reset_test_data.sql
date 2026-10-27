-- Guarded test-profile reset. Running this script only previews the profile and performs no delete.
-- To explicitly delete the test profile, change @confirm_reset to the exact phrase below,
-- review the preview, then run the script again. Cascading foreign keys remove only this
-- player's campaign progress, tutorial stars and inventory. This never drops tables/schema.
USE sourcerer_crusade;

SET @player_to_reset = CONVERT('Sourcerer' USING utf8mb4) COLLATE utf8mb4_unicode_ci;
SET @confirm_reset = CONVERT('NO' USING utf8mb4) COLLATE utf8mb4_unicode_ci;

SELECT p.player_id, p.player_name, p.xp, p.character_level, p.coins,
       (SELECT COUNT(*) FROM player_progress pp WHERE pp.player_id = p.player_id) AS campaign_rows,
       (SELECT COUNT(*) FROM tutorial_progress tp WHERE tp.player_id = p.player_id) AS tutorial_rows,
       (SELECT COUNT(*) FROM inventory i WHERE i.player_id = p.player_id) AS inventory_rows
FROM players p
WHERE p.player_name = @player_to_reset;

SET @reset_sql = IF(@confirm_reset = CONVERT('DELETE TEST PLAYER Sourcerer' USING utf8mb4)
                              COLLATE utf8mb4_unicode_ci,
    'DELETE FROM players WHERE player_name = ''Sourcerer''',
    'SELECT ''No rows deleted. Set @confirm_reset to the exact confirmation phrase to reset the test profile.'' AS reset_status');
PREPARE reset_statement FROM @reset_sql;
EXECUTE reset_statement;
DEALLOCATE PREPARE reset_statement;
