package it.rpg.engine;

import it.rpg.model.Character;
import it.rpg.model.Choice;
import it.rpg.model.GameState;
import it.rpg.model.Outcome;
import it.rpg.model.Scene;

import java.util.List;

/**
 * The contract the user interface programs against. Depending on this
 * abstraction rather than a concrete implementation (Dependency Inversion)
 * means the UI never needs to know how the game rules actually work, and the
 * rules can evolve or be swapped without touching the views.
 */
public interface GameEngine {

    /** Begins a brand-new play-through using the story's starting profile. */
    void startNewGame(String playerName);

    /** Resumes a previously saved play-through. */
    void restore(GameState state);

    Scene currentScene();

    Character character();

    /** All choices declared by the current scene (selectable or not). */
    List<Choice> currentChoices();

    /** @return {@code true} if every requirement of the choice is met. */
    boolean canSelect(Choice choice);

    /**
     * Takes a choice: applies its effects, then moves to the target scene.
     *
     * @throws IllegalStateException if the choice is not currently selectable
     *                               or the game is already over.
     */
    void choose(Choice choice);

    /**
     * @return the total experience required to reach the next level, or {@code 0}
     *         if the character is at the maximum level or the story has no curve.
     */
    int xpForNextLevel();

    /** @return {@code true} while the current scene's battle is unresolved. */
    boolean inCombat();

    /** @return the active battle, or {@code null} when not in combat. */
    Combat combat();

    /**
     * Resolves one round of the active battle.
     *
     * @throws IllegalStateException if there is no battle in progress.
     */
    void attack();

    /**
     * Abandons the active battle, moving to the encounter's flee scene.
     *
     * @throws IllegalStateException if there is no battle, or fleeing is not allowed.
     */
    void flee();

    boolean isGameOver();

    Outcome outcome();

    /** Builds a snapshot of the current state, ready to be persisted. */
    GameState snapshot(String saveName);

    void addListener(GameEventListener listener);

    void removeListener(GameEventListener listener);
}
