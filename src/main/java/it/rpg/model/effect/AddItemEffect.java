package it.rpg.model.effect;

import it.rpg.model.Character;
import it.rpg.model.Item;

/** Grants an {@link Item} to the player (no-op if already owned). */
public record AddItemEffect(Item item) implements Effect {

    @Override
    public void applyTo(Character character) {
        character.addItem(item);
    }

    @Override
    public String describe() {
        return "Ottieni: " + item.name();
    }
}
