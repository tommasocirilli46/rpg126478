package it.rpg.model.requirement;

import it.rpg.model.Character;

/**
 * A precondition that the player's {@link Character} must satisfy for a
 * {@code Choice} to be selectable (e.g. owning an item, or having an attribute
 * above a threshold).
 *
 * <p>Like {@code Effect}, this is an <strong>extension point</strong>: new kinds
 * of requirement are added as new implementations without touching existing
 * code (Open/Closed Principle).</p>
 */
public interface Requirement {

    /** @return {@code true} if the character meets this requirement. */
    boolean isSatisfiedBy(Character character);

    /** A short, human-readable description shown to the player when unmet. */
    String describe();
}
