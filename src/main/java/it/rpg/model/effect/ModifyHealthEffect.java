package it.rpg.model.effect;

import it.rpg.model.Character;

/** Adds {@code amount} (possibly negative) to the character's health. */
public record ModifyHealthEffect(int amount) implements Effect {

    @Override
    public void applyTo(Character character) {
        character.modifyHealth(amount);
    }

    @Override
    public String describe() {
        return (amount >= 0 ? "+" : "") + amount + " salute";
    }
}
