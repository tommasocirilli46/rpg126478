package it.rpg.engine;

import it.rpg.model.Character;
import it.rpg.model.Choice;
import it.rpg.model.LevelingPolicy;
import it.rpg.model.Outcome;
import it.rpg.model.Scene;
import it.rpg.model.StoryGraph;
import it.rpg.model.effect.Effect;
import it.rpg.model.effect.GainExperienceEffect;
import it.rpg.model.requirement.MinLevelRequirement;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelAndXpTest {

    @Test
    void gainExperienceEffectAccumulatesXp() {
        Character c = new Character("Eroe", 20);
        new GainExperienceEffect(40).applyTo(c);
        new GainExperienceEffect(35).applyTo(c);
        assertEquals(75, c.xp());
        assertEquals(1, c.level());
    }

    @Test
    void levelingPolicyPromotesAndAppliesAttributeBonus() {
        LevelingPolicy policy = new LevelingPolicy(100, Map.of("coraggio", 1), 5);
        Character c = new Character("Eroe", 20);
        c.setAttribute("coraggio", 3);
        c.addExperience(100);

        int gained = policy.applyTo(c);

        assertEquals(1, gained);
        assertEquals(2, c.level());
        assertEquals(4, c.attribute("coraggio"));
    }

    @Test
    void levelingPolicyCanGrantSeveralLevelsAtOnceUpToMax() {
        LevelingPolicy policy = new LevelingPolicy(100, Map.of("coraggio", 1), 3);
        Character c = new Character("Eroe", 20);
        c.addExperience(10_000); // far beyond the cap

        int gained = policy.applyTo(c);

        assertEquals(2, gained);          // level 1 -> 3, capped at maxLevel
        assertEquals(3, c.level());
        assertEquals(2, c.attribute("coraggio"));
    }

    @Test
    void minLevelRequirementChecksThreshold() {
        Character c = new Character("Eroe", 20);
        assertFalse(new MinLevelRequirement(2).isSatisfiedBy(c));
        c.setLevel(2);
        assertTrue(new MinLevelRequirement(2).isSatisfiedBy(c));
    }

    @Test
    void engineAppliesLevelingWhenAChoiceGrantsXp() {
        Choice train = new Choice("Allenati", "fine",
                List.of(), List.of((Effect) new GainExperienceEffect(120)));
        Scene start = new Scene("inizio", "Inizio", "...", Outcome.NONE, false, List.of(train));
        Scene end = new Scene("fine", "Fine", "...", Outcome.VICTORY, true, List.of());

        LevelingPolicy policy = new LevelingPolicy(100, Map.of("coraggio", 1, "intelligenza", 1), 5);
        StoryGraph story = new StoryGraph("xp", "Storia XP", "", "inizio",
                new StoryGraph.StartProfile(20, Map.of("coraggio", 1, "intelligenza", 1), List.of()),
                List.of(start, end), List.of(), policy);

        GameEngine engine = new StoryGameEngine(story);
        engine.startNewGame("Eroe");
        engine.choose(engine.currentChoices().get(0));

        assertEquals(120, engine.character().xp());
        assertEquals(2, engine.character().level());
        assertEquals(2, engine.character().attribute("coraggio"));
        assertEquals(2, engine.character().attribute("intelligenza"));
    }
}
