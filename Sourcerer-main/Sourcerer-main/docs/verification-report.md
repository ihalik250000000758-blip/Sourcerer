# Sourcerer: Crusade SQL and persistence verification

Date: 2026-10-27

## Checks

| Check | Status | Evidence |
|---|---|---|
| Java source audit | PASS | `LevelBank` defines seven campaign levels; `StageBank` defines six tutorial minigames; no campaign level is marked as a boss. |
| SQL scripts 01–06 | PASS | Executed in order on an isolated MariaDB 10.4.32 instance. The reset script ran in preview mode and deleted no rows. |
| Schema and JDBC agreement | PASS | `JdbcGameStore` save/load and tutorial-star SQL completed on the disposable database. |
| Player save/load | PASS | Round trip restored level 2, 25 XP, 40 coins, one Healing Potion, zero Mana Potions, and three campaign stars. |
| Tutorial minigame save/load | PASS | Two tutorial stars survived reload; the one-time tutorial flag reloaded as false. |
| Verification queries | PASS | `05_verification_queries.sql` showed the sample player values, seven campaign rows, six tutorial rows, table list, and column metadata. |
| New save potion rule | PASS | The new database profile loaded with zero Healing and Mana Potions before test rewards were added. |
| Maven tests | PASS | Clean test/package run: 4 tests, 0 failures, 0 errors. |
| Maven package | PASS | Clean build created `target/sourcerer-crusade-1.0.0.jar`. |
| Live database safety | PASS | Only new MariaDB datadirs on ports 33077 and 33078 were used; both servers are stopped. No SQL was run against the user's configured/live database. |

## Implemented behavior

- The database stores the single local player, XP, character level, coins,
  potion quantities, seven campaign-level unlock/completion/star rows, and six
  separate tutorial-minigame star rows.
- Current campaign level is derived from the first incomplete level, matching
  `Progress.currentLevel()`.
- The one-time introductory tutorial flag is not read from or written to either
  save format.
- New saves start with zero potions. Explicit quantities in existing saves are
  kept. Tutorial stars in the legacy tutorial properties file are imported into
  database mode when the database has no tutorial-star progress.
- Boss tables and boss counts are omitted because none of the seven current
  campaign levels is a boss.
- Reset SQL defaults to a preview. It requires an explicit confirmation phrase
  before deleting the named test profile.

## Limits

The SQL was run against MariaDB 10.4.32 in a disposable local datadir, not
against every MariaDB release or any existing user schema. `CREATE TABLE IF NOT
EXISTS` does not transform older tables; an existing schema created from a
different version should be backed up and reviewed before applying the scripts.
The application still uses the fixed local profile name `Sourcerer`.
