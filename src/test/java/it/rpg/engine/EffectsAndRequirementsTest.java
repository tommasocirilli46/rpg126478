package it.rpg.engine;

import it.rpg.model.Character;
import it.rpg.model.Item;
import it.rpg.model.effect.AddItemEffect;
import it.rpg.model.effect.ModifyHealthEffect;
import it.rpg.model.requirement.HasItemRequirement;
import it.rpg.model.requirement.MinAttributeRequirement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EffectsAndRequirementsTest {

    @Test
    void modifyHealthIsClampedToMaximum() {
        Character c = new Character("Eroe", 30);
        c.modifyHealth(-10);
        new ModifyHealthEffect(50).applyTo(c); // would overshoot the maximum
        assertEquals(30, c.health());
    }

    @Test
    void modifyHealthNeverGoesBelowZero() {
        Character c = new Character("Eroe", 30);
        new ModifyHealthEffect(-1000).applyTo(c);
        assertEquals(0, c.health());
        assertFalse(c.isAlive());
    }

    @Test
    void addItemEffectGrantsItem() {
        Character c = new Character("Eroe", 30);
        new AddItemEffect(new Item("torcia", "Torcia", "")).applyTo(c);
        assertTrue(c.hasItem("torcia"));
    }

    @Test
    void hasItemRequirementReflectsInventory() {
        Character c = new Character("Eroe", 30);
        HasItemRequirement req = new HasItemRequirement("chiave", "Chiave");
        assertFalse(req.isSatisfiedBy(c));
        c.addItem(new Item("chiave", "Chiave", ""));
        assertTrue(req.isSatisfiedBy(c));
    }

    @Test
    void minAttributeRequirementChecksThreshold() {
        Character c = new Character("Eroe", 30);
        c.setAttribute("coraggio", 3);
        assertTrue(new MinAttributeRequirement("coraggio", 3).isSatisfiedBy(c));
        assertFalse(new MinAttributeRequirement("coraggio", 4).isSatisfiedBy(c));
    }
}
