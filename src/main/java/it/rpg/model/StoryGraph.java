package it.rpg.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An entire adventure: metadata, the starting profile of the hero and all the
 * {@link Scene}s indexed by id. Stories are pure <em>content</em> (data), so a
 * new adventure can be shipped as a new JSON file without writing any code.
 */
public final class StoryGraph {

    /** Initial values used to build a fresh {@link Character} for a new game. */
    public static final class StartProfile {
        private int health = 20;
        private Map<String, Integer> attributes = new HashMap<>();
        private List<Item> items = new ArrayList<>();

        private StartProfile() {
        }

        public StartProfile(int health, Map<String, Integer> attributes, List<Item> items) {
            this.health = Math.max(1, health);
            this.attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);
            this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
        }

        public int health() {
            return health;
        }

        public Map<String, Integer> attributes() {
            return attributes == null ? Map.of() : attributes;
        }

        public List<Item> items() {
            return items == null ? List.of() : items;
        }
    }

    private String id;
    private String title;
    private String description;
    private String startSceneId;
    private StartProfile start = new StartProfile();
    private List<Scene> scenes = new ArrayList<>();
    private List<Npc> npcs = new ArrayList<>();
    private LevelingPolicy leveling;

    /** Lazily-built indexes; {@code transient} so the JSON layer ignores them. */
    private transient Map<String, Scene> index;
    private transient Map<String, Npc> npcIndex;

    private StoryGraph() {
    }

    public StoryGraph(String id, String title, String description, String startSceneId,
                      StartProfile start, List<Scene> scenes) {
        this(id, title, description, startSceneId, start, scenes, List.of(), null);
    }

    public StoryGraph(String id, String title, String description, String startSceneId,
                      StartProfile start, List<Scene> scenes, List<Npc> npcs, LevelingPolicy leveling) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = Objects.requireNonNull(title, "title");
        this.description = description == null ? "" : description;
        this.startSceneId = Objects.requireNonNull(startSceneId, "startSceneId");
        this.start = start == null ? new StartProfile() : start;
        this.scenes = scenes == null ? new ArrayList<>() : new ArrayList<>(scenes);
        this.npcs = npcs == null ? new ArrayList<>() : new ArrayList<>(npcs);
        this.leveling = leveling;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public StartProfile start() {
        return start == null ? new StartProfile() : start;
    }

    public Scene startScene() {
        return scene(startSceneId);
    }

    /**
     * @return the scene with the given id.
     * @throws IllegalArgumentException if no scene with that id exists.
     */
    public Scene scene(String sceneId) {
        if (index == null) {
            index = new HashMap<>();
            for (Scene s : scenes) {
                index.put(s.id(), s);
            }
        }
        Scene scene = index.get(sceneId);
        if (scene == null) {
            throw new IllegalArgumentException("Scena inesistente: " + sceneId);
        }
        return scene;
    }

    /** The experience curve for this story, or a no-op policy if none is defined. */
    public LevelingPolicy leveling() {
        return leveling == null ? new LevelingPolicy(0, Map.of(), 1) : leveling;
    }

    /**
     * @return the NPC with the given id.
     * @throws IllegalArgumentException if no NPC with that id exists.
     */
    public Npc npc(String npcId) {
        if (npcIndex == null) {
            npcIndex = new HashMap<>();
            for (Npc n : npcs) {
                npcIndex.put(n.id(), n);
            }
        }
        Npc npc = npcIndex.get(npcId);
        if (npc == null) {
            throw new IllegalArgumentException("PNG inesistente: " + npcId);
        }
        return npc;
    }
}
