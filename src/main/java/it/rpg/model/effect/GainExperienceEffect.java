package it.rpg.model.effect;

import it.rpg.model.Character;

/**
 * Grants {@code amount} experience points to the character. The effect only adds
 * raw experience; whether that triggers a level-up is decided by the story's
 * {@code LevelingPolicy}, applied by the engine after the effects run.
 */
public record GainExperienceEffect(int amount) implements Effect {

    @Override
    public void applyTo(Character character) {
        character.addExperience(amount);
    }

    @Override
    public String describe() {
        return "+" + amount + " XP";
    }
}
