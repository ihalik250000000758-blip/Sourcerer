# Gameplay and manual checks

1. Launch from the menu and select **Play** to open the campaign map. The game
   has seven main levels, from Variables through Functions. Stage 1 is open;
   each later level unlocks when the previous level is completed.
2. Solve each challenge by arranging the code tiles and typing the answer.
   Hints, undo, clear, battle actions, and the syntax dictionary remain
   available.
3. A win records stars and XP. The first clear awards 25 coins and one Healing
   Potion. Replaying can improve stars, but does not repeat the first-clear
   reward.
4. Open **Potion Store** from the map. Healing and Mana potions are consumed in
   battle only when they can have an effect. New saves start with zero potions.
5. Complete the six tutorial minigames to record their separate star progress.
   The one-time introductory tutorial prompt is shown again after restarting.
6. Quit and reopen the game to confirm level, XP, coins, inventory, campaign
   stars, and tutorial minigame stars are restored.

The current seven-level campaign has no boss encounters.

## Persistence modes

- Without `SOURCERER_DB_URL`, campaign and player progress are stored in
  `sourcerer_progress.properties`; tutorial minigame stars are stored in
  `sourcerer_tutorial.properties` in the working directory.
- With `SOURCERER_DB_URL`, player state and both kinds of stars are stored in
  the `Sourcerer` database profile. Run all setup SQL first. A connection or
  schema error is reported instead of replacing saved state with defaults.

See [database-setup.md](database-setup.md) for Workbench setup and verification.
