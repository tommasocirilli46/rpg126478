package it.rpg.persistence;

/** Metadata about a persisted save, used to populate the load screen. */
public record SaveInfo(String saveName, String storyId, long savedAt) {
}
