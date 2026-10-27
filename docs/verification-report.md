# Verification report

Date: 2026-10-22

## Summary

The source tree now uses Maven's standard Java layout. It preserves the
original seven campaign stages and existing tutorial, battle, dictionary, and
navigation classes, extends the campaign to 100 stages, adds ten boss
challenges and boss enemies, provides a potion store, and persists campaign and
player state in local properties or opt-in MySQL mode.

## Existing feature inventory

The baseline had 45 Java files, seven authored campaign stages, a separate
training stage, six standalone tutorial stages, a guided tutorial, code tile
assembly plus typed-answer checks, hints, undo/clear, a syntax dictionary,
Swing menu/map/battle/result screens, Syntax Spider and Null Pointer enemies,
HP/MP, Syntax Strike, potions, XP, and star progress. The baseline direct
Java 21 compile succeeded. Source review confirms the original stage data and
screens remain. The final desktop UI was not manually exercised in this run.

## Verification

| Check | Status | Evidence |
|---|---|---|
| Baseline compile | PASS | Direct `javac` compile before edits completed with exit code 0. |
| Final Java compile | PASS | `javac --release 21 -encoding UTF-8` over `src/main/java` completed with no errors. |
| Maven tests | PASS | Clean Maven test/package lifecycle with a temporary local-repository settings file; 4 tests, 0 failures, 0 errors. |
| Maven package | PASS | Same clean run built `target/sourcerer-crusade-1.0.0.jar`. |
| 100 stages and ten bosses | PASS | Test checks count, stage IDs, boss markers, answer sizes, and puzzle token matching. |
| First-clear rewards | PASS | Test checks replay does not grant duplicate coins or potion. |
| Local save/load | PASS | Test reloads XP/currency/inventory and boss progress from an isolated properties file. |
| Store and potion model | PASS | Tests check insufficient funds, successful charge, inventory increment/reload, potion consumption, and invalid coin changes. |
| SQL execution | NOT RUN | Scripts were not run against the local database. |
| JDBC connection and database restart | NOT RUN | No database credentials were assumed and no live database test was performed. |
| GUI visual/manual regression | NOT RUN | The Swing app was not opened interactively in this verification run. |
| Transaction rollback against MySQL | NOT RUN | JDBC transaction paths are implemented but require a configured schema/server to exercise. |

## Acceptance matrix

| ID | Status | Notes |
|---|---|---|
| F-01 Feature preservation | PASS | Original authored stages and screen/tutorial classes remain; compile and targeted regression coverage passed. |
| F-02 Maven refactoring | PASS | Standard `src/main/java` and `src/test/java`; package builds. |
| F-03 Package organization | PASS | Existing `sourcerer.model`, `.tutorial`, `.ui` retained; `.util` added. |
| F-04 Database connection | PARTIAL | Configurable `DatabaseConnection` and JDBC save layer are implemented; actual connection not tested. |
| F-05 Expanded world map | PASS | 100 stages, paged ten at a time; locked, completed, current, and boss styling. |
| F-06 Player XP | PASS | Existing XP thresholds retained and state persists. |
| F-07 Player coins | PASS | First-clear rewards, purchases, validation, and local/JDBC persistence implemented. |
| F-08 Potion store | PASS | UI reads real player state; purchases charge coins, add inventory, and persist. |
| F-09 Boss stages | PASS | Stages 10–100 by tens have extra challenges and scaled boss enemies. |
| F-10 Boss statistics | PASS | Unique defeated boss stages and total count persist in file/JDBC state. |
| F-11 Inventory | PASS | Potion use and purchase update persisted inventory; model tests cover purchase/reload. |
| F-12 SQL scripts | PARTIAL | Six scripts match the Java persistence tables; live server compatibility remains unverified. |
| F-13 Workbench guide | PASS | Beginner script order, server check, connection setup, launch, and verification steps documented. |
| F-14 Regression testing | PARTIAL | Targeted tests pass; full interactive regression of all original workflows was not run. |
| F-15 Documentation | PASS | README, implementation notes, gameplay guide, database guide, and this report are present. |

## Manual verification still needed

1. Confirm the local MySQL server version and port, then run scripts 01–05 in
   Workbench in order. Do not run script 06 except against disposable test data.
2. Set the database environment variables and launch with `mvn exec:java`.
3. Complete a first-clear stage, buy a potion, defeat a boss, run the SQL
   verification queries, restart, and confirm the same values reload.
4. Open the game without database variables and manually check the original
   tutorial, dictionary, stage map, battle actions, and results workflow.

## Remaining limitations

- The JDBC profile is a single fixed `Sourcerer` player; there is no account
  picker or multi-player UI.
- JDBC load/save calls currently run synchronously from the Swing workflow. A
  slow or unavailable database can pause the UI while a call times out.
- `operation_history` is created and queryable, but the current JDBC layer does
  not yet append purchase/reward audit rows. First-clear rewards are guarded by
  saved stage completion, and multi-record saves use a transaction.
- Database mode, SQL version compatibility, transaction rollback, and visual
  UI behavior remain unverified against the user's live setup.

## Workspace state

This workspace has no `.git` directory, so branch, commit, and changed-file
status are unavailable. No files were committed or pushed. The MySQL scripts
were not executed and no player data was changed.
