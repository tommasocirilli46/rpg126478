package it.rpg.model.effect;

import it.rpg.model.Character;

/**
 * A consequence of taking a {@code Choice}, applied to the player's
 * {@link Character} (e.g. losing health, gaining an item, raising an attribute).
 *
 * <p>This is the main <strong>extension point</strong> of the game rules: a new
 * kind of effect is added by creating a new implementation, with no change to
 * the engine, the model or the UI (Open/Closed Principle). The persistence
 * layer maps each implementation to/from a JSON {@code "type"} discriminator.</p>
 */
public interface Effect {

    /** Applies this effect to the given character, mutating its state. */
    void applyTo(Character character);

    /** A short, human-readable description, useful for logs or feedback. */
    String describe();
}
