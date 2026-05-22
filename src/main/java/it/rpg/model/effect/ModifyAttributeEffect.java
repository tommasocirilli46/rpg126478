package it.rpg.model.effect;

import it.rpg.model.Character;

/** Adds {@code amount} (possibly negative) to a named attribute. */
public record ModifyAttributeEffect(String attribute, int amount) implements Effect {

    @Override
    public void applyTo(Character character) {
        character.modifyAttribute(attribute, amount);
    }

    @Override
    public String describe() {
        return (amount >= 0 ? "+" : "") + amount + " " + attribute;
    }
}
