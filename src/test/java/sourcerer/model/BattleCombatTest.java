package sourcerer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BattleCombatTest {

    @Test
    void campaignEnemyHpScalesGraduallyWithLevel() {
        int[] expectedHp = {40, 75, 90, 125, 140, 175, 190};
        assertEquals(LevelBank.count(), expectedHp.length);
        for (int level = 1; level <= expectedHp.length; level++) {
            assertEquals(expectedHp[level - 1], LevelBank.get(level).createBug().getMaxHp(),
                    "level " + level);
        }
    }

    @Test
    void answerSubmissionsCostManaAndSkillCostsMore() {
        Battle battle = new Battle();
        battle.startLevel(LevelBank.get(1));
        int startingMana = battle.getPlayer().getMp();

        Puzzle puzzle = battle.getPuzzle();
        battle.submit(puzzle.getSolutionCode(), puzzle.getSolutionCode());
        assertEquals(startingMana - Battle.BASIC_ATTACK_COST, battle.getPlayer().getMp());

        battle.useSkill();
        assertTrue(battle.isStrikeArmed());
        assertEquals(startingMana - Battle.BASIC_ATTACK_COST - Battle.SKILL_COST, battle.getPlayer().getMp());
        assertTrue(Battle.SKILL_COST > Battle.BASIC_ATTACK_COST);
    }

    @Test
    void skillIsRejectedWhenManaIsInsufficient() {
        Battle battle = new Battle();
        battle.getPlayer().drainMana(battle.getPlayer().getMp() - (Battle.SKILL_COST - 1));

        battle.useSkill();

        assertFalse(battle.isStrikeArmed());
        assertEquals(Battle.SKILL_COST - 1, battle.getPlayer().getMp());
    }

    @Test
    void basicAttackUsesRemainingManaWithoutGoingNegative() {
        Battle battle = new Battle();
        battle.startLevel(LevelBank.get(1));
        battle.getPlayer().drainMana(battle.getPlayer().getMp() - 1);
        Puzzle puzzle = battle.getPuzzle();

        battle.submit(puzzle.getSolutionCode(), puzzle.getSolutionCode());

        assertEquals(0, battle.getPlayer().getMp());
        assertEquals(100, battle.getPlayer().getHp(), "positive mana at submission must not trigger the HP penalty");
    }

    @Test
    void zeroManaCostsTwoHpOnCorrectAndIncorrectSubmissions() {
        Battle correctBattle = new Battle();
        correctBattle.startLevel(LevelBank.get(1));
        correctBattle.getPlayer().drainMana(correctBattle.getPlayer().getMp());
        int correctHp = correctBattle.getPlayer().getHp();
        Puzzle correctPuzzle = correctBattle.getPuzzle();

        correctBattle.submit(correctPuzzle.getSolutionCode(), correctPuzzle.getSolutionCode());

        assertEquals(correctHp - Battle.ZERO_MANA_HP_COST, correctBattle.getPlayer().getHp());

        Battle incorrectBattle = new Battle();
        incorrectBattle.startLevel(LevelBank.get(1));
        incorrectBattle.getPlayer().drainMana(incorrectBattle.getPlayer().getMp());
        int incorrectHp = incorrectBattle.getPlayer().getHp();

        incorrectBattle.submit("wrong", "wrong");

        int enemyDamage = Math.max(1, incorrectBattle.getBug().getAttackPower() + 2 - 2);
        assertEquals(incorrectHp - Battle.ZERO_MANA_HP_COST - enemyDamage,
                incorrectBattle.getPlayer().getHp());
    }

    @Test
    void zeroManaPenaltyImmediatelyDefeatsPlayerAndBlocksFurtherActions() {
        Battle battle = new Battle();
        battle.startLevel(LevelBank.get(1));
        battle.getPlayer().drainMana(battle.getPlayer().getMp());
        battle.getPlayer().drainHp(battle.getPlayer().getHp() - Battle.ZERO_MANA_HP_COST);
        Puzzle puzzle = battle.getPuzzle();

        assertEquals(Battle.Outcome.DEFEAT,
                battle.submit(puzzle.getSolutionCode(), puzzle.getSolutionCode()));
        assertEquals(0, battle.getPlayer().getHp());
        assertEquals(Battle.Outcome.DEFEAT, battle.submit(puzzle.getSolutionCode(), puzzle.getSolutionCode()));
        battle.useSkill();
        battle.grantLevelReward();
        assertEquals(0, battle.getPlayer().getPotionCount(Potion.HEALING));
    }

    @Test
    void enemyTurnDefeatsPlayerWhenRetaliationReducesHpToZero() {
        Battle battle = new Battle();
        battle.startLevel(LevelBank.get(1));
        battle.getPlayer().drainHp(battle.getPlayer().getHp() - 7);

        assertEquals(Battle.Outcome.DEFEAT, battle.submit("wrong", "wrong"));
        assertEquals(0, battle.getPlayer().getHp());
        assertTrue(battle.drainLog().stream().anyMatch(message -> message.contains("You have been defeated")));
    }

    @Test
    void defeatedEnemyEndsBattleAndCannotTakeTurnsOrGrantDuplicateRewards() {
        Battle battle = new Battle();
        battle.startLevel(LevelBank.get(1));
        Battle.Outcome outcome = Battle.Outcome.CONTINUE;

        while (outcome == Battle.Outcome.CONTINUE) {
            Puzzle puzzle = battle.getPuzzle();
            outcome = battle.submit(puzzle.getSolutionCode(), puzzle.getSolutionCode());
        }

        assertEquals(Battle.Outcome.VICTORY, outcome);
        assertTrue(battle.getBug().isDefeated());
        int hpAfterVictory = battle.getPlayer().getHp();
        int manaAfterVictory = battle.getPlayer().getMp();
        int xpAfterVictory = battle.getPlayer().getXp();

        assertEquals(Battle.Outcome.VICTORY, battle.submit("wrong", "wrong"));
        battle.useSkill();
        battle.grantLevelReward();
        battle.grantLevelReward();

        assertEquals(hpAfterVictory, battle.getPlayer().getHp());
        assertEquals(manaAfterVictory, battle.getPlayer().getMp());
        assertEquals(xpAfterVictory, battle.getPlayer().getXp());
        assertEquals(1, battle.getPlayer().getPotionCount(Potion.HEALING));
    }

    @Test
    void enemyDefeatBeforeTheLastPuzzleEndsTheBattleImmediately() {
        Battle battle = new Battle();
        battle.startLevel(LevelBank.get(1));
        battle.getBug().takeDamage(battle.getBug().getHp() - 1);
        int playerHp = battle.getPlayer().getHp();
        Puzzle puzzle = battle.getPuzzle();

        assertEquals(Battle.Outcome.VICTORY,
                battle.submit(puzzle.getSolutionCode(), puzzle.getSolutionCode()));
        assertTrue(battle.getBug().isDefeated());
        assertEquals(playerHp, battle.getPlayer().getHp(), "a defeated enemy must not retaliate");
        assertEquals(1, battle.getPuzzleNumber(), "the battle must not advance to another puzzle");
    }
}
