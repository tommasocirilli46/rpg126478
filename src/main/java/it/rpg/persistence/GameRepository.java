package it.rpg.persistence;

import it.rpg.model.GameState;

import java.util.List;
import java.util.Optional;

/**
 * Persistence of play-throughs. The contract is storage-agnostic: the default
 * implementation writes JSON files to the local disk, but a cloud or database
 * backend could be added simply by implementing this interface.
 */
public interface GameRepository {

    /** Creates or overwrites the save identified by {@link GameState#saveName()}. */
    void save(GameState state);

    /** @return the save with the given name, or empty if none exists. */
    Optional<GameState> load(String saveName);

    /** @return metadata about all existing saves, most recent first. */
    List<SaveInfo> listSaves();

    /** Deletes the save with the given name (no-op if it does not exist). */
    void delete(String saveName);
}
