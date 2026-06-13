package it.rpg.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A node of the story: a piece of narrative text plus the {@link Choice}s that
 * lead away from it. A scene flagged as {@code ending} terminates the
 * play-through, with its {@link Outcome} describing how. A scene carrying an
 * {@link Encounter} is a battle: its choices are replaced by combat actions
 * until the fight is resolved.
 */
public final class Scene {

    private String id;
    private String title;
    private String text;
    private String image;
    private Outcome outcome = Outcome.NONE;
    private boolean ending = false;
    private List<Choice> choices = new ArrayList<>();
    private Encounter encounter;

    private Scene() {
    }

    public Scene(String id, String title, String text, Outcome outcome, boolean ending, List<Choice> choices) {
        this(id, title, text, outcome, ending, choices, null);
    }

    public Scene(String id, String title, String text, Outcome outcome, boolean ending,
                 List<Choice> choices, Encounter encounter) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = Objects.requireNonNull(title, "title");
        this.text = Objects.requireNonNull(text, "text");
        this.outcome = outcome == null ? Outcome.NONE : outcome;
        this.ending = ending;
        this.choices = choices == null ? new ArrayList<>() : new ArrayList<>(choices);
        this.encounter = encounter;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String text() {
        return text;
    }

    /**
     * @return the file name of this scene's illustration, looked up on the
     *         classpath under {@code /images/}, or {@code null} if the scene
     *         has no picture.
     */
    public String image() {
        return image;
    }

    public Outcome outcome() {
        return outcome == null ? Outcome.NONE : outcome;
    }

    public boolean isEnding() {
        return ending;
    }

    public List<Choice> choices() {
        return Collections.unmodifiableList(choices == null ? List.of() : choices);
    }

    /** @return the battle hosted by this scene, or {@code null} if there is none. */
    public Encounter encounter() {
        return encounter;
    }

    public boolean hasEncounter() {
        return encounter != null;
    }
}
