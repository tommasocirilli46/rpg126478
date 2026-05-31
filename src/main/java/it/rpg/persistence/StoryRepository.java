package it.rpg.persistence;

import it.rpg.model.StoryGraph;

import java.util.List;

/**
 * Read-only access to the available stories. The interface hides where the
 * content actually lives (bundled JSON resources today, a remote service or a
 * database tomorrow): swapping the source means writing a new implementation,
 * not editing the engine or the UI.
 */
public interface StoryRepository {

    /** @return descriptors of every story that can be played. */
    List<StoryInfo> listAvailable();

    /**
     * Loads a full story by id.
     *
     * @throws PersistenceException if the story cannot be found or parsed.
     */
    StoryGraph load(String storyId);
}
