package it.rpg.model.requirement;

import it.rpg.model.Character;

/** Satisfied when the character's level is greater than or equal to {@code value}. */
public record MinLevelRequirement(int value) implements Requirement {

    @Override
    public boolean isSatisfiedBy(Character character) {
        return character.level() >= value;
    }

    @Override
    public String describe() {
        return "Richiede livello ≥ " + value;
    }
}
