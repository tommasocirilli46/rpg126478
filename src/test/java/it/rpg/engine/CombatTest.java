package it.rpg.engine;

import it.rpg.model.Character;
import it.rpg.model.Encounter;
import it.rpg.model.LevelingPolicy;
import it.rpg.model.Npc;
import it.rpg.model.Outcome;
import it.rpg.model.Scene;
import it.rpg.model.StoryGraph;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatTest {

    /**
     * A one-scene battle story: it starts directly inside the encounter. Tuning
     * the parameters lets each test drive the fight to a specific outcome.
     */
    private StoryGraph combatStory(int npcHealth, int npcAttack, int playerCoraggio,
                                   int playerHealth, int xpReward, boolean withFlee) {
        Npc enemy = new Npc("nemico", "Nemico", "", npcHealth, npcAttack);
        Encounter encounter = new Encounter("nemico", "vittoria",
                withFlee ? "fuga" : null, xpReward, "coraggio");

        Scene fight = new Scene("scontro", "Scontro", "Combatti!",
                Outcome.NONE, false, List.of(), encounter);
        Scene victory = new Scene("vittoria", "Vittoria", "Hai vinto.",
                Outcome.VICTORY, true, List.of());
        Scene flee = new Scene("fuga", "Fuga", "Sei scappato.",
                Outcome.NONE, false, List.of());

        StoryGraph.StartProfile profile = new StoryGraph.StartProfile(
                playerHealth, Map.of("coraggio", playerCoraggio), List.of());
        LevelingPolicy policy = new LevelingPolicy(100, Map.of("coraggio", 1), 5);
        return new StoryGraph("combat", "Storia di combattimento", "", "scontro",
                profile, List.of(fight, victory, flee), List.of(enemy), policy);
    }

    @Test
    void combatResolvesDeterministically() {
        Character hero = new Character("Eroe", 20);
        hero.setAttribute("coraggio", 5);
        Combat combat = new Combat(hero, new Npc("n", "Nemico", "", 10, 3), "coraggio");

        CombatRound first = combat.playerAttack();
        assertEquals(5, first.damageDealt());
        assertEquals(3, first.damageTaken());
        assertEquals(5, first.enemyHealth());
        assertEquals(17, first.playerHealth());
        assertFalse(combat.isOver());

        CombatRound second = combat.playerAttack();
        assertEquals(0, second.enemyHealth());
        assertEquals(0, second.damageTaken()); // no counter-attack once the enemy falls
        assertTrue(combat.isOver());
        assertTrue(combat.playerWon());
        assertThrows(IllegalStateException.class, combat::playerAttack);
    }

    @Test
    void enteringAnEncounterSceneStartsCombat() {
        GameEngine engine = new StoryGameEngine(combatStory(10, 3, 5, 20, 100, true));
        engine.startNewGame("Eroe");

        assertTrue(engine.inCombat());
        assertEquals("Nemico", engine.combat().enemyName());
        assertTrue(engine.currentChoices().isEmpty());
        assertFalse(engine.isGameOver());
    }

    @Test
    void winningAwardsXpLevelsUpAndMovesToVictory() {
        GameEngine engine = new StoryGameEngine(combatStory(10, 3, 5, 20, 100, true));
        engine.startNewGame("Eroe");

        engine.attack(); // enemy 10 -> 5, hero 20 -> 17
        assertTrue(engine.inCombat());
        engine.attack(); // enemy 5 -> 0, victory

        assertFalse(engine.inCombat());
        assertEquals("vittoria", engine.currentScene().id());
        assertTrue(engine.isGameOver());
        assertEquals(Outcome.VICTORY, engine.outcome());
        assertEquals(100, engine.character().xp());
        assertEquals(2, engine.character().level());
        assertEquals(6, engine.character().attribute("coraggio")); // 5 + level-up bonus
    }

    @Test
    void dyingInCombatIsADefeat() {
        GameEngine engine = new StoryGameEngine(combatStory(100, 50, 1, 20, 100, true));
        engine.startNewGame("Eroe");

        engine.attack(); // enemy survives and hits for 50 -> hero dies

        assertFalse(engine.inCombat());
        assertFalse(engine.character().isAlive());
        assertTrue(engine.isGameOver());
        assertEquals(Outcome.DEFEAT, engine.outcome());
        assertEquals(0, engine.character().xp()); // no reward for losing
    }

    @Test
    void fleeingLeavesCombatForTheFleeScene() {
        GameEngine engine = new StoryGameEngine(combatStory(10, 3, 5, 20, 100, true));
        engine.startNewGame("Eroe");

        engine.flee();

        assertFalse(engine.inCombat());
        assertEquals("fuga", engine.currentScene().id());
        assertFalse(engine.isGameOver());
    }

    @Test
    void fleeingIsRejectedWhenTheEncounterForbidsIt() {
        GameEngine engine = new StoryGameEngine(combatStory(10, 3, 5, 20, 100, false));
        engine.startNewGame("Eroe");

        assertThrows(IllegalStateException.class, engine::flee);
    }
}
