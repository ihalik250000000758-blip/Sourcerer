# Gameplay and manual checks

1. Launch from the menu and select **Play** to open the paged world map. Stage 1
   is open; locked stages require completion of the prior stage. Boss stages use
   a distinct orange node.
2. Solve each challenge by arranging the code tiles and typing the same Python
   statement. Existing hints, undo, clear, battle actions, and the syntax
   dictionary remain available.
3. A win records stars, XP, and a first-clear reward. The next stage unlocks
   after the stage is completed. Replaying can improve stars but does not grant
   the first-clear coins or potion again.
4. Open **Potion Store** from the map. It shows prices and owned quantities.
   Purchases fail without enough coins. Healing and Mana potions are consumed
   from battle only when they can have an effect.
5. Every tenth stage includes a harder second Python puzzle. Boss records and
   the total number of unique boss stages defeated are saved.
6. Quit and reopen the game to confirm level, XP, coins, inventory, stage state,
   and boss progress are restored.

## Persistence modes

- Without `SOURCERER_DB_URL`, progress is stored in
  `sourcerer_progress.properties` in the working directory. Keep this file to
  keep the save.
- With `SOURCERER_DB_URL`, the application loads and saves the single local
  `Sourcerer` profile through JDBC. Run all setup SQL first. A database
  connection or schema error is reported rather than replaced by defaults.

See [database-setup.md](database-setup.md) for Workbench setup and verification.
