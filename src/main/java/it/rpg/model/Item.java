package it.rpg.model;

import java.util.Objects;

/**
 * An object the player can carry. Items are identified by their {@code id};
 * two items with the same id are considered equal regardless of their label.
 *
 * <p>This is a plain mutable-free class (not a {@code record}) so that the JSON
 * persistence layer can reconstruct it via reflection without relying on
 * record support or extra module access.</p>
 */
public final class Item {

    private String id;
    private String name;
    private String description;

    /** Required by the JSON layer; not intended for direct use. */
    private Item() {
    }

    public Item(String id, String name, String description) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.description = description == null ? "" : description;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Item other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
