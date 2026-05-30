package it.rpg.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.rpg.model.GameState;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * {@link GameRepository} that stores each save as a pretty-printed JSON file in
 * a local directory (by default {@code <user.home>/.narrative-rpg/saves}).
 *
 * <p>Saves are matched by the {@code saveName} stored <em>inside</em> the file
 * rather than by file name, so listing and loading stay correct even after the
 * name has been sanitised for the filesystem.</p>
 */
public final class JsonGameRepository implements GameRepository {

    private final Path baseDir;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /** Uses the default location under the user's home directory. */
    public JsonGameRepository() {
        this(Paths.get(System.getProperty("user.home"), ".narrative-rpg", "saves"));
    }

    /** Uses a custom directory (useful for tests). */
    public JsonGameRepository(Path baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void save(GameState state) {
        try {
            Files.createDirectories(baseDir);
            Path file = baseDir.resolve(fileName(state.saveName()));
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                gson.toJson(state, writer);
            }
        } catch (IOException e) {
            throw new PersistenceException("Impossibile salvare la partita: " + state.saveName(), e);
        }
    }

    @Override
    public Optional<GameState> load(String saveName) {
        return readAll().stream()
                .filter(s -> s.saveName().equals(saveName))
                .findFirst();
    }

    @Override
    public List<SaveInfo> listSaves() {
        List<SaveInfo> infos = new ArrayList<>();
        for (GameState s : readAll()) {
            infos.add(new SaveInfo(s.saveName(), s.storyId(), s.savedAt()));
        }
        infos.sort(Comparator.comparingLong(SaveInfo::savedAt).reversed());
        return infos;
    }

    @Override
    public void delete(String saveName) {
        if (!Files.isDirectory(baseDir)) {
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(baseDir, "*.json")) {
            for (Path file : stream) {
                GameState s = tryRead(file);
                if (s != null && s.saveName().equals(saveName)) {
                    Files.deleteIfExists(file);
                }
            }
        } catch (IOException e) {
            throw new PersistenceException("Impossibile eliminare la partita: " + saveName, e);
        }
    }

    // --- internals -----------------------------------------------------------

    private List<GameState> readAll() {
        List<GameState> result = new ArrayList<>();
        if (!Files.isDirectory(baseDir)) {
            return result;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(baseDir, "*.json")) {
            for (Path file : stream) {
                GameState s = tryRead(file);
                if (s != null) {
                    result.add(s);
                }
            }
        } catch (IOException e) {
            throw new PersistenceException("Impossibile leggere i salvataggi", e);
        }
        return result;
    }

    private GameState tryRead(Path file) {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, GameState.class);
        } catch (Exception e) {
            // A corrupt or unrelated file should not break the whole listing.
            return null;
        }
    }

    private String fileName(String saveName) {
        return saveName.replaceAll("[^a-zA-Z0-9-_ ]", "_").trim() + ".json";
    }
}
