package it.rpg.model;

import it.rpg.model.effect.Effect;
import it.rpg.model.requirement.Requirement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A single branch the player can take from a {@link Scene}: a label, the id of
 * the scene it leads to, the {@link Requirement}s that gate it and the
 * {@link Effect}s applied when it is taken.
 *
 * <p>Immutable from the caller's perspective: the exposed lists are
 * unmodifiable. The private no-arg constructor exists only for the JSON layer.</p>
 */
public final class Choice {

    private String text;
    private String target;
    private List<Requirement> requirements = new ArrayList<>();
    private List<Effect> effects = new ArrayList<>();

    private Choice() {
    }

    public Choice(String text, String target, List<Requirement> requirements, List<Effect> effects) {
        this.text = Objects.requireNonNull(text, "text");
        this.target = Objects.requireNonNull(target, "target");
        this.requirements = requirements == null ? new ArrayList<>() : new ArrayList<>(requirements);
        this.effects = effects == null ? new ArrayList<>() : new ArrayList<>(effects);
    }

    public String text() {
        return text;
    }

    /** @return the id of the scene this choice leads to. */
    public String target() {
        return target;
    }

    public List<Requirement> requirements() {
        return Collections.unmodifiableList(requirements == null ? List.of() : requirements);
    }

    public List<Effect> effects() {
        return Collections.unmodifiableList(effects == null ? List.of() : effects);
    }
}
