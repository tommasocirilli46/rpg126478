package it.rpg.model.requirement;

import it.rpg.model.Character;

/** Satisfied when the named attribute is greater than or equal to {@code value}. */
public record MinAttributeRequirement(String attribute, int value) implements Requirement {

    @Override
    public boolean isSatisfiedBy(Character character) {
        return character.attribute(attribute) >= value;
    }

    @Override
    public String describe() {
        return "Richiede " + attribute + " \u2265 " + value;
    }
}
