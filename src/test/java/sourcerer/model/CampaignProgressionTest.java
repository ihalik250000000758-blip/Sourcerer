package sourcerer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

class CampaignProgressionTest {
    private final Path temp = Path.of("target", "test-data");

    @AfterEach
    void cleanSaveFiles() throws IOException {
        Files.deleteIfExists(temp.resolve("save.properties"));
        Files.deleteIfExists(temp.resolve("save.properties.tmp"));
        Files.deleteIfExists(temp.resolve("shop.properties"));
        Files.deleteIfExists(temp.resolve("shop.properties.tmp"));
    }

    private Path savePath(String name) throws IOException {
        Files.createDirectories(temp);
        return temp.resolve(name);
    }

    @Test
    void keepsTheSevenAuthoredCampaignStagesAndHasNoBossLevels() {
        assertEquals(7, LevelBank.count());
        assertEquals("Variables", LevelBank.get(1).getTitle());
        assertEquals("Functions", LevelBank.get(7).getTitle());
        assertEquals(0, LevelBank.all().stream().filter(Level::isBoss).count());
        for (int id = 1; id <= LevelBank.count(); id++) {
            assertFalse(LevelBank.get(id).isBoss(), "stage " + id);
            for (Puzzle puzzle : LevelBank.get(id).getPuzzles()) {
                assertTrue(puzzle.getSolutionTokens().size() <= Puzzle.GRID_SIZE, "stage " + id);
                assertTrue(puzzle.matches(puzzle.getSolutionCode()), "stage " + id + ": " + puzzle.getPrompt());
            }
        }
    }

    @Test
    void firstClearRewardIsSavedOnceAndProgressSurvivesReload() throws IOException {
        Path file = savePath("save.properties");
        Progress progress = new Progress(file.toFile());
        Player player = new Player("Sourcerer");
        progress.restorePlayer(player);
        player.gainXp(50);

        assertTrue(progress.settleStage(1, 3, player, false));
        assertEquals(25, player.getCoins());
        assertEquals(1, player.getPotionCount(Potion.HEALING));
        assertFalse(progress.settleStage(1, 2, player, false));
        assertEquals(25, player.getCoins());
        assertEquals(1, player.getPotionCount(Potion.HEALING));

        progress.settleStage(2, 2, player, false);
        Progress restored = new Progress(file.toFile());
        Player restoredPlayer = new Player("Sourcerer");
        restored.restorePlayer(restoredPlayer);
        assertEquals(0, restored.getBossesDefeated());
        assertFalse(restored.isBossDefeated(10));
        assertEquals(50, restoredPlayer.getCoins());
        assertEquals(2, restoredPlayer.getPotionCount(Potion.HEALING));
        assertEquals(2, restoredPlayer.getLevel());
        assertEquals(3, restored.getStars(1));
        assertEquals(2, restored.getStars(2));
    }

    @Test
    void purchasesRequireFundsAndUpdatePersistentInventory() throws IOException {
        Path file = savePath("shop.properties");
        Progress progress = new Progress(file.toFile());
        Player player = new Player("Sourcerer");
        progress.restorePlayer(player);
        assertFalse(progress.purchasePotion(player, Potion.MANA));
        assertEquals(0, player.getPotionCount(Potion.MANA));
        player.addCoins(20);
        assertTrue(progress.purchasePotion(player, Potion.MANA));
        assertEquals(0, player.getCoins());
        assertEquals(1, player.getPotionCount(Potion.MANA));

        Player reload = new Player("Sourcerer");
        new Progress(file.toFile()).restorePlayer(reload);
        assertEquals(0, reload.getCoins());
        assertEquals(1, reload.getPotionCount(Potion.MANA));
    }

    @Test
    void potionUseConsumesOnlyWhenItHasAnEffectAndCoinsRejectInvalidChanges() {
        Player player = new Player("Sourcerer");
        assertFalse(player.usePotion(Potion.MANA));
        assertEquals(0, player.getPotionCount(Potion.MANA));
        player.addPotion(Potion.HEALING);
        player.takeDamage(40);
        assertTrue(player.usePotion(Potion.HEALING));
        assertEquals(0, player.getPotionCount(Potion.HEALING));
        player.addCoins(-10);
        assertFalse(player.spendCoins(-10));
        assertEquals(0, player.getCoins());
    }
}
