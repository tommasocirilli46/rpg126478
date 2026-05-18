package it.rpg.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The player's avatar and the single source of truth for everything that can
 * change during a play-through: health, named attributes (e.g. "coraggio",
 * "intelligenza") and the inventory.
 *
 * <p>The class is intentionally free of any game-flow logic. It only knows how
 * to mutate its own state in a consistent way (for instance, health is always
 * clamped between 0 and {@code maxHealth}). Deciding <em>when</em> those
 * mutations happen is the responsibility of the {@code Effect}s and the engine.</p>
 */
public final class Character {

    private String name;
    private int health;
    private int maxHealth;
    private int xp;
    private int level = 1;
    private Map<String, Integer> attributes = new HashMap<>();
    private List<Item> inventory = new ArrayList<>();

    /** Required by the JSON layer; not intended for direct use. */
    private Character() {
    }

    public Character(String name, int maxHealth) {
        this.name = Objects.requireNonNull(name, "name");
        this.maxHealth = Math.max(1, maxHealth);
        this.health = this.maxHealth;
    }

    public String name() {
        return name;
    }

    public int health() {
        return health;
    }

    public int maxHealth() {
        return maxHealth;
    }

    public boolean isAlive() {
        return health > 0;
    }

    /** Applies a delta to health, clamping the result to {@code [0, maxHealth]}. */
    public void modifyHealth(int delta) {
        this.health = Math.max(0, Math.min(maxHealth, health + delta));
    }

    /** Total experience accumulated so far. */
    public int xp() {
        return xp;
    }

    /**
     * Current level. Normalised to a minimum of {@code 1} so that saves created
     * before levelling existed (where the field defaults to {@code 0}) keep
     * working.
     */
    public int level() {
        return Math.max(1, level);
    }

    /**
     * Adds raw experience points. This is a pure mutation: deciding whether the
     * accumulated experience is enough to level up — and what a level grants —
     * is a per-story policy applied elsewhere (see {@code LevelingPolicy}).
     */
    public void addExperience(int amount) {
        this.xp += Math.max(0, amount);
    }

    /** Sets the current level; used by the levelling policy when leveling up. */
    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public int attribute(String key) {
        return attributes.getOrDefault(key, 0);
    }

    public void setAttribute(String key, int value) {
        attributes.put(Objects.requireNonNull(key, "key"), value);
    }

    public void modifyAttribute(String key, int delta) {
        setAttribute(key, attribute(key) + delta);
    }

    public Map<String, Integer> attributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public List<Item> inventory() {
        return Collections.unmodifiableList(inventory);
    }

    public boolean hasItem(String itemId) {
        return inventory.stream().anyMatch(i -> i.id().equals(itemId));
    }

    public void addItem(Item item) {
        Objects.requireNonNull(item, "item");
        if (!hasItem(item.id())) {
            inventory.add(item);
        }
    }

    public void removeItem(String itemId) {
        inventory.removeIf(i -> i.id().equals(itemId));
    }
}
