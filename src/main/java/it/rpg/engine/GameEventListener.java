package it.rpg.engine;

import it.rpg.model.Character;
import it.rpg.model.Npc;
import it.rpg.model.Outcome;
import it.rpg.model.Scene;

/**
 * Observer notified by the {@link GameEngine} when the game state changes. This
 * keeps the engine completely independent of any specific user interface: a
 * JavaFX view, a CLI, a web layer or a test can all observe the same engine.
 *
 * <p>All methods are {@code default} (no-ops) so an implementer overrides only
 * the events it cares about (Interface Segregation in spirit).</p>
 */
public interface GameEventListener {

    /** Fired when a new scene becomes the current one. */
    default void onSceneEntered(Scene scene) {
    }

    /** Fired when the character's state changed (health, attributes, inventory). */
    default void onCharacterChanged(Character character) {
    }

    /** Fired once when the play-through ends. */
    default void onGameOver(Outcome outcome) {
    }

    /** Fired when entering a scene starts a battle against {@code enemy}. */
    default void onCombatStarted(Npc enemy) {
    }

    /** Fired after each exchange of blows during a battle. */
    default void onCombatRound(CombatRound round) {
    }

    /** Fired once when a battle ends, before the story moves on. */
    default void onCombatEnded(boolean playerWon) {
    }

    /** Fired when accumulated experience pushes the character to {@code newLevel}. */
    default void onLevelUp(int newLevel) {
    }
}
