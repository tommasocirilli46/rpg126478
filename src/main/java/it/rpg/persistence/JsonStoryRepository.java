package it.rpg.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.rpg.model.StoryGraph;
import it.rpg.model.effect.Effect;
import it.rpg.model.requirement.Requirement;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * {@link StoryRepository} backed by JSON files bundled on the classpath under
 * {@code /stories}. A manifest ({@code /stories/index.json}) lists the
 * available stories; each story lives in {@code /stories/<id>.json}.
 *
 * <p>The Gson instance is configured with the polymorphic {@code Effect} and
 * {@code Requirement} adapters, so the rest of the code only ever sees domain
 * objects.</p>
 */
public final class JsonStoryRepository implements StoryRepository {

    private static final String INDEX = "/stories/index.json";

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Effect.class, new EffectDeserializer())
            .registerTypeAdapter(Requirement.class, new RequirementDeserializer())
            .create();

    @Override
    public List<StoryInfo> listAvailable() {
        try (Reader reader = open(INDEX)) {
            Type listType = new TypeToken<List<StoryInfo>>() {
            }.getType();
            List<StoryInfo> stories = gson.fromJson(reader, listType);
            return stories == null ? List.of() : stories;
        } catch (Exception e) {
            throw new PersistenceException("Impossibile leggere l'elenco delle storie", e);
        }
    }

    @Override
    public StoryGraph load(String storyId) {
        String path = "/stories/" + storyId + ".json";
        try (Reader reader = open(path)) {
            StoryGraph story = gson.fromJson(reader, StoryGraph.class);
            if (story == null) {
                throw new PersistenceException("Storia vuota o non valida: " + storyId);
            }
            return story;
        } catch (PersistenceException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Impossibile caricare la storia: " + storyId, e);
        }
    }

    private Reader open(String resourcePath) {
        InputStream in = getClass().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new PersistenceException("Risorsa non trovata: " + resourcePath);
        }
        return new InputStreamReader(in, StandardCharsets.UTF_8);
    }
}
