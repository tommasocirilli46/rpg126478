package it.rpg.model;

import java.util.Objects;

/**
 * A non-player character defined by the story: a guardian, a beast, a rival.
 * NPCs are pure <em>content</em> — immutable data declared once in the
 * {@link StoryGraph} registry and referenced by id from an {@link Encounter}.
 * Their volatile battle state (current health) lives in the engine's combat, not
 * here, so the same definition can be fought more than once.
 */
public final class Npc {

    private String id;
    private String name;
    private String description;
    private int maxHealth;
    private int attack;

    private Npc() {
    }

    public Npc(String id, String name, String description, int maxHealth, int attack) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.description = description == null ? "" : description;
        this.maxHealth = Math.max(1, maxHealth);
        this.attack = Math.max(0, attack);
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description == null ? "" : description;
    }

    public int maxHealth() {
        return Math.max(1, maxHealth);
    }

    /** Damage this NPC deals to the player each round it survives. */
    public int attack() {
        return Math.max(0, attack);
    }
}
