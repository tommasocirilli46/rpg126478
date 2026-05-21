package it.rpg.model.requirement;

import it.rpg.model.Character;

/** Satisfied when the character's health is greater than or equal to {@code value}. */
public record MinHealthRequirement(int value) implements Requirement {

    @Override
    public boolean isSatisfiedBy(Character character) {
        return character.health() >= value;
    }

    @Override
    public String describe() {
        return "Richiede salute \u2265 " + value;
    }
}
