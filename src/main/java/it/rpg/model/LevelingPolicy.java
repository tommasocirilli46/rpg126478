package it.rpg.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The per-story rule that turns accumulated experience into levels. Keeping this
 * here — and out of {@link Character} — lets the character stay a pure state
 * holder while each adventure decides its own growth curve as plain JSON data.
 *
 * <p>A character advances from level {@code L} to {@code L+1} as soon as its
 * total experience reaches {@code L * xpPerLevel}; every level gained applies
 * {@code attributeBonus} to the character's attributes, up to {@code maxLevel}.
 * A policy with {@code xpPerLevel <= 0} never levels anyone up (the default for
 * stories that don't define one).</p>
 */
public final class LevelingPolicy {

    private int xpPerLevel;
    private Map<String, Integer> attributeBonus = new HashMap<>();
    private int maxLevel = 1;

    private LevelingPolicy() {
    }

    public LevelingPolicy(int xpPerLevel, Map<String, Integer> attributeBonus, int maxLevel) {
        this.xpPerLevel = xpPerLevel;
        this.attributeBonus = attributeBonus == null ? new HashMap<>() : new HashMap<>(attributeBonus);
        this.maxLevel = Math.max(1, maxLevel);
    }

    public int xpPerLevel() {
        return xpPerLevel;
    }

    public Map<String, Integer> attributeBonus() {
        return attributeBonus == null ? Map.of() : attributeBonus;
    }

    public int maxLevel() {
        return Math.max(1, maxLevel);
    }

    /** @return the total experience required to reach the level after {@code level}. */
    public int xpForNextLevel(int level) {
        return Math.max(1, level) * xpPerLevel;
    }

    /**
     * Promotes the character as many times as its experience allows, applying the
     * attribute bonus once per level gained.
     *
     * @return the number of levels gained (0 if none).
     */
    public int applyTo(Character character) {
        if (xpPerLevel <= 0) {
            return 0;
        }
        int gained = 0;
        while (character.level() < maxLevel() && character.xp() >= xpForNextLevel(character.level())) {
            character.setLevel(character.level() + 1);
            attributeBonus().forEach(character::modifyAttribute);
            gained++;
        }
        return gained;
    }
}
