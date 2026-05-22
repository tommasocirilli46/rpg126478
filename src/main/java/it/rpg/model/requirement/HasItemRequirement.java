package it.rpg.model.requirement;

import it.rpg.model.Character;

/** Satisfied when the character owns the item with the given id. */
public record HasItemRequirement(String itemId, String label) implements Requirement {

    @Override
    public boolean isSatisfiedBy(Character character) {
        return character.hasItem(itemId);
    }

    @Override
    public String describe() {
        return "Richiede: " + (label == null || label.isBlank() ? itemId : label);
    }
}
