-- Schema audit for fresh installs and repeat runs. Constraints and indexes are created
-- with the tables in 02_create_tables.sql; this script makes no changes to player data.
USE sourcerer_crusade;

SELECT TABLE_NAME, CONSTRAINT_NAME, CONSTRAINT_TYPE
FROM information_schema.TABLE_CONSTRAINTS
WHERE CONSTRAINT_SCHEMA = DATABASE()
  AND TABLE_NAME IN ('players', 'stages', 'player_progress', 'tutorial_stages',
                     'tutorial_progress', 'inventory')
ORDER BY TABLE_NAME, CONSTRAINT_NAME;

SELECT TABLE_NAME, INDEX_NAME, COLUMN_NAME, NON_UNIQUE, SEQ_IN_INDEX
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME IN ('players', 'stages', 'player_progress', 'tutorial_stages',
                     'tutorial_progress', 'inventory')
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;
