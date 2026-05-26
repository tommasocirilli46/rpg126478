package it.rpg.engine;

import it.rpg.model.Character;
import it.rpg.model.Choice;
import it.rpg.model.Encounter;
import it.rpg.model.GameState;
import it.rpg.model.Item;
import it.rpg.model.LevelingPolicy;
import it.rpg.model.Npc;
import it.rpg.model.Outcome;
import it.rpg.model.Scene;
import it.rpg.model.StoryGraph;
import it.rpg.model.effect.Effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default {@link GameEngine} driven by a {@link StoryGraph}. It owns the
 * current scene and the {@link Character}, validates and applies choices, runs
 * the {@link Encounter} battles, applies the story's experience curve and
 * notifies its listeners. It has no dependency on persistence or on the UI.
 */
public final class StoryGameEngine implements GameEngine {

    private final StoryGraph story;
    private final List<GameEventListener> listeners = new ArrayList<>();

    private Character character;
    private Scene currentScene;
    private Combat combat;

    public StoryGameEngine(StoryGraph story) {
        this.story = Objects.requireNonNull(story, "story");
    }

    @Override
    public void startNewGame(String playerName) {
        StoryGraph.StartProfile start = story.start();
        Character hero = new Character(playerName, start.health());
        for (Map.Entry<String, Integer> e : start.attributes().entrySet()) {
            hero.setAttribute(e.getKey(), e.getValue());
        }
        for (Item item : start.items()) {
            hero.addItem(item);
        }
        this.character = hero;
        enter(story.startScene());
        fireCharacterChanged();
        maybeGameOver();
    }

    @Override
    public void restore(GameState state) {
        Objects.requireNonNull(state, "state");
        this.character = state.character();
        enter(story.scene(state.currentSceneId()));
        fireCharacterChanged();
        maybeGameOver();
    }

    @Override
    public Scene currentScene() {
        return currentScene;
    }

    @Override
    public Character character() {
        return character;
    }

    @Override
    public List<Choice> currentChoices() {
        if (inCombat() || currentScene == null) {
            return List.of();
        }
        return currentScene.choices();
    }

    @Override
    public boolean canSelect(Choice choice) {
        return choice.requirements().stream().allMatch(r -> r.isSatisfiedBy(character));
    }

    @Override
    public void choose(Choice choice) {
        Objects.requireNonNull(choice, "choice");
        if (inCombat()) {
            throw new IllegalStateException("Un combattimento è in corso.");
        }
        if (isGameOver()) {
            throw new IllegalStateException("La partita è terminata.");
        }
        if (!currentChoices().contains(choice)) {
            throw new IllegalStateException("La scelta non appartiene alla scena corrente.");
        }
        if (!canSelect(choice)) {
            throw new IllegalStateException("Requisiti non soddisfatti per questa scelta.");
        }
        for (Effect effect : choice.effects()) {
            effect.applyTo(character);
        }
        applyLeveling();
        fireCharacterChanged();
        enter(story.scene(choice.target()));
        maybeGameOver();
    }

    @Override
    public int xpForNextLevel() {
        LevelingPolicy policy = story.leveling();
        if (policy.xpPerLevel() <= 0 || character.level() >= policy.maxLevel()) {
            return 0;
        }
        return policy.xpForNextLevel(character.level());
    }

    // --- combat --------------------------------------------------------------

    @Override
    public boolean inCombat() {
        return combat != null;
    }

    @Override
    public Combat combat() {
        return combat;
    }

    @Override
    public void attack() {
        if (combat == null) {
            throw new IllegalStateException("Nessun combattimento in corso.");
        }
        Encounter encounter = currentScene.encounter();
        CombatRound round = combat.playerAttack();
        fireCombatRound(round);
        fireCharacterChanged();

        if (!combat.isOver()) {
            return;
        }
        boolean won = combat.playerWon();
        if (won) {
            character.addExperience(encounter.xpReward());
            applyLeveling();
            fireCharacterChanged();
        }
        combat = null;
        fireCombatEnded(won);
        if (won) {
            enter(story.scene(encounter.victorySceneId()));
        }
        maybeGameOver();
    }

    @Override
    public void flee() {
        if (combat == null) {
            throw new IllegalStateException("Nessun combattimento in corso.");
        }
        Encounter encounter = currentScene.encounter();
        if (!encounter.canFlee()) {
            throw new IllegalStateException("Non puoi fuggire da questo scontro.");
        }
        combat = null;
        enter(story.scene(encounter.fleeSceneId()));
        maybeGameOver();
    }

    @Override
    public boolean isGameOver() {
        return !character.isAlive() || (!inCombat() && currentScene != null && currentScene.isEnding());
    }

    @Override
    public Outcome outcome() {
        if (!character.isAlive()) {
            return Outcome.DEFEAT;
        }
        return currentScene == null ? Outcome.NONE : currentScene.outcome();
    }

    @Override
    public GameState snapshot(String saveName) {
        return new GameState(saveName, story.id(), currentScene.id(), character);
    }

    @Override
    public void addListener(GameEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    // --- internals -----------------------------------------------------------

    private void enter(Scene scene) {
        this.currentScene = scene;
        this.combat = null;
        for (GameEventListener l : List.copyOf(listeners)) {
            l.onSceneEntered(scene);
        }
        if (scene.hasEncounter()) {
            startCombat(scene.encounter());
        }
    }

    private void startCombat(Encounter encounter) {
        Npc enemy = story.npc(encounter.npcId());
        this.combat = new Combat(character, enemy, encounter.playerAttackAttribute());
        for (GameEventListener l : List.copyOf(listeners)) {
            l.onCombatStarted(enemy);
        }
    }

    /** Applies the story's experience curve, notifying listeners of any level gained. */
    private void applyLeveling() {
        int gained = story.leveling().applyTo(character);
        if (gained > 0) {
            for (GameEventListener l : List.copyOf(listeners)) {
                l.onLevelUp(character.level());
            }
        }
    }

    private void fireCharacterChanged() {
        for (GameEventListener l : List.copyOf(listeners)) {
            l.onCharacterChanged(character);
        }
    }

    private void fireCombatRound(CombatRound round) {
        for (GameEventListener l : List.copyOf(listeners)) {
            l.onCombatRound(round);
        }
    }

    private void fireCombatEnded(boolean playerWon) {
        for (GameEventListener l : List.copyOf(listeners)) {
            l.onCombatEnded(playerWon);
        }
    }

    private void maybeGameOver() {
        if (isGameOver()) {
            Outcome outcome = outcome();
            for (GameEventListener l : List.copyOf(listeners)) {
                l.onGameOver(outcome);
            }
        }
    }
}
