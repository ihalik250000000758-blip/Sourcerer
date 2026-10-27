# Implementation and preservation notes

## Baseline inventory

The project began as a plain Java Swing application in package `sourcerer`,
with source files directly under `src/sourcerer`. It had no Maven descriptor,
database layer, SQL scripts, README, or automated tests. The baseline direct
Java 21 compile completed successfully.

Existing functionality inspected and retained:

- Seven authored campaign stages: Variables, Data Types, Input & Output,
  Conditionals, Loops, Lists, and Functions.
- Separate guided tutorial and six standalone interactive tutorial stages.
- Tile assembly plus typed answer validation, hints, undo, clear, feedback,
  battle log, and tutorial overlays.
- Swing menu, world map, battle, result, tutorial, and syntax dictionary screens.
- Syntax Spider and Null Pointer enemies, HP/MP, Syntax Strike, potion use,
  puzzle XP, victory XP, and 1–3 star grading.
- Campaign stars and tutorial completion previously used local properties files.

## Changes

The original package and entry point are retained under standard Maven source
directories. The campaign has the seven lessons in `LevelBank`; the current
source does not define boss campaign stages.

Player progression saves XP, character level, coins, potion quantities, and
campaign stars. Tutorial minigame stars are saved separately. The one-time
introductory tutorial flag is transient. New saves start with zero potions.
With no database URL, progress uses properties files; with
`SOURCERER_DB_URL`, JDBC uses the schema documented in `sql/` and
`docs/database-setup.md`. Setup and reset SQL are never invoked by the game.

## Reward rule change

The original game granted one Healing Potion for every victory, including a
replay, and puzzle/victory XP could be earned again on replay. The new campaign
keeps XP for each correctly solved puzzle, while coins and one Healing Potion
are first-clear-only. This adds an earned-currency source and prevents replay
farming of first-clear rewards as required by the specification.

## Workbench workflow

Follow [database-setup.md](database-setup.md) to start the local service, verify
the server/port, run the numbered scripts, inspect tables, set credentials, and
confirm saved player and tutorial progress after restart.
