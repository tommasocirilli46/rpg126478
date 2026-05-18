package it.rpg.model;

import java.util.Objects;

/**
 * A serializable snapshot of a play-through: which story is being played, the
 * current scene and the full {@link Character} state. This is exactly what the
 * persistence layer reads and writes — nothing about the UI or the engine
 * leaks into the saved data.
 */
public final class GameState {

    private String saveName;
    private String storyId;
    private String currentSceneId;
    private Character character;
    private long savedAt;

    private GameState() {
    }

    public GameState(String saveName, String storyId, String currentSceneId, Character character) {
        this.saveName = Objects.requireNonNull(saveName, "saveName");
        this.storyId = Objects.requireNonNull(storyId, "storyId");
        this.currentSceneId = Objects.requireNonNull(currentSceneId, "currentSceneId");
        this.character = Objects.requireNonNull(character, "character");
        this.savedAt = System.currentTimeMillis();
    }

    public String saveName() {
        return saveName;
    }

    public String storyId() {
        return storyId;
    }

    public String currentSceneId() {
        return currentSceneId;
    }

    public Character character() {
        return character;
    }

    public long savedAt() {
        return savedAt;
    }
}
