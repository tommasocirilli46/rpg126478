package it.rpg.engine;

import it.rpg.model.Choice;
import it.rpg.model.GameState;
import it.rpg.model.Item;
import it.rpg.model.Outcome;
import it.rpg.model.Scene;
import it.rpg.model.StoryGraph;
import it.rpg.model.effect.ModifyHealthEffect;
import it.rpg.model.requirement.HasItemRequirement;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StoryGameEngineTest {

    /** Builds a tiny story: start -> end, with one open choice and one locked choice. */
    private StoryGraph sampleStory() {
        Choice open = new Choice("Avanza", "fine",
                List.of(), List.of(new ModifyHealthEffect(-5)));
        Choice locked = new Choice("Porta chiusa", "fine",
                List.of(new HasItemRequirement("chiave", "Chiave")), List.of());
        Scene start = new Scene("inizio", "Inizio", "Testo iniziale.",
                Outcome.NONE, false, List.of(open, locked));
        Scene end = new Scene("fine", "Fine", "Hai vinto.",
                Outcome.VICTORY, true, List.of());

        StoryGraph.StartProfile profile =
                new StoryGraph.StartProfile(20, Map.of("coraggio", 2), List.of());
        return new StoryGraph("test", "Storia di test", "", "inizio",
                profile, List.of(start, end));
    }

    @Test
    void startNewGameInitialisesCharacterFromProfile() {
        GameEngine engine = new StoryGameEngine(sampleStory());
        engine.startNewGame("Eroe");

        assertEquals("Eroe", engine.character().name());
        assertEquals(20, engine.character().health());
        assertEquals(2, engine.character().attribute("coraggio"));
        assertEquals("inizio", engine.currentScene().id());
        assertFalse(engine.isGameOver());
    }

    @Test
    void requirementGatesChoiceSelectability() {
        GameEngine engine = new StoryGameEngine(sampleStory());
        engine.startNewGame("Eroe");

        Choice open = engine.currentChoices().get(0);
        Choice locked = engine.currentChoices().get(1);

        assertTrue(engine.canSelect(open));
        assertFalse(engine.canSelect(locked));
    }

    @Test
    void choosingAppliesEffectsAndAdvancesToVictory() {
        GameEngine engine = new StoryGameEngine(sampleStory());
        engine.startNewGame("Eroe");

        engine.choose(engine.currentChoices().get(0));

        assertEquals(15, engine.character().health());
        assertEquals("fine", engine.currentScene().id());
        assertTrue(engine.isGameOver());
        assertEquals(Outcome.VICTORY, engine.outcome());
    }

    @Test
    void choosingALockedChoiceThrows() {
        GameEngine engine = new StoryGameEngine(sampleStory());
        engine.startNewGame("Eroe");

        Choice locked = engine.currentChoices().get(1);
        assertThrows(IllegalStateException.class, () -> engine.choose(locked));
    }

    @Test
    void runningOutOfHealthIsADefeat() {
        Choice fatal = new Choice("Trappola", "altrove",
                List.of(), List.of(new ModifyHealthEffect(-100)));
        Scene start = new Scene("inizio", "Inizio", "...", Outcome.NONE, false, List.of(fatal));
        Scene elsewhere = new Scene("altrove", "Altrove", "...", Outcome.NONE, false, List.of());
        StoryGraph story = new StoryGraph("d", "Morte", "", "inizio",
                new StoryGraph.StartProfile(10, Map.of(), List.of()), List.of(start, elsewhere));

        GameEngine engine = new StoryGameEngine(story);
        engine.startNewGame("Eroe");
        engine.choose(engine.currentChoices().get(0));

        assertFalse(engine.character().isAlive());
        assertTrue(engine.isGameOver());
        assertEquals(Outcome.DEFEAT, engine.outcome());
    }

    @Test
    void snapshotAndRestorePreserveProgress() {
        GameEngine engine = new StoryGameEngine(sampleStory());
        engine.startNewGame("Eroe");
        engine.character().addItem(new Item("chiave", "Chiave", ""));
        GameState snapshot = engine.snapshot("slot1");

        GameEngine restored = new StoryGameEngine(sampleStory());
        restored.restore(snapshot);

        assertEquals("inizio", restored.currentScene().id());
        assertTrue(restored.character().hasItem("chiave"));
        assertEquals("slot1", snapshot.saveName());
    }
}
