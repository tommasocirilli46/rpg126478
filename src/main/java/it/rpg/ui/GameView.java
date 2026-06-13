package it.rpg.ui;

import it.rpg.engine.Combat;
import it.rpg.engine.CombatRound;
import it.rpg.engine.GameEngine;
import it.rpg.engine.GameEventListener;
import it.rpg.model.Character;
import it.rpg.model.Choice;
import it.rpg.model.Item;
import it.rpg.model.Npc;
import it.rpg.model.Outcome;
import it.rpg.model.Scene;
import it.rpg.persistence.GameRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The gameplay screen. It registers as a {@link GameEventListener} so it
 * re-renders automatically whenever the engine changes state, and it never
 * touches the game rules directly — it only calls the {@link GameEngine}
 * abstraction.
 */
final class GameView implements GameEventListener {

    private final GameApp app;
    private final GameEngine engine;
    private final GameRepository gameRepository;

    private final Label sceneTitle = new Label();
    private final ImageView sceneImage = new ImageView();
    private final Label narrative = new Label();
    private final VBox choiceBox = new VBox(10);
    private final VBox characterPanel = new VBox(10);
    private final Button saveButton = new Button("Salva");

    /** Last line of combat feedback, shown above the battle actions. */
    private String combatLog = "";

    GameView(GameApp app, GameEngine engine, GameRepository gameRepository) {
        this.app = app;
        this.engine = engine;
        this.gameRepository = gameRepository;
        this.engine.addListener(this);
    }

    BorderPane view() {
        sceneTitle.getStyleClass().add("scene-title");
        sceneTitle.setWrapText(true);

        sceneImage.getStyleClass().add("scene-image");
        sceneImage.setPreserveRatio(true);
        sceneImage.setSmooth(true);
        sceneImage.setFitHeight(220);

        narrative.getStyleClass().add("narrative");
        narrative.setWrapText(true);
        VBox narrativeContent = new VBox(14, sceneImage, narrative);
        narrativeContent.setAlignment(Pos.TOP_CENTER);
        ScrollPane narrativeScroll = new ScrollPane(narrativeContent);
        narrativeScroll.setFitToWidth(true);
        narrativeScroll.getStyleClass().add("narrative-scroll");
        VBox.setVgrow(narrativeScroll, Priority.ALWAYS);

        VBox center = new VBox(16, narrativeScroll, choiceBox);
        center.setPadding(new Insets(20));

        characterPanel.getStyleClass().add("character-panel");
        characterPanel.setPadding(new Insets(18));
        characterPanel.setPrefWidth(240);

        saveButton.getStyleClass().add("ghost-button");
        saveButton.setOnAction(e -> save());
        Button menu = new Button("Menu");
        menu.getStyleClass().add("ghost-button");
        menu.setOnAction(e -> {
            engine.removeListener(this);
            app.showMainMenu();
        });
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bottom = new HBox(12, saveButton, spacer, menu);
        bottom.setPadding(new Insets(14, 20, 18, 20));
        bottom.setAlignment(Pos.CENTER_LEFT);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-root");
        root.setTop(sceneTitle);
        BorderPane.setMargin(sceneTitle, new Insets(20, 20, 0, 20));
        root.setCenter(center);
        root.setRight(characterPanel);
        root.setBottom(bottom);

        // Initial render is pulled from the engine, because the new-game / restore
        // events were fired before this view registered as a listener.
        renderScene(engine.currentScene());
        renderCharacter(engine.character());
        if (engine.isGameOver()) {
            showGameOver(engine.outcome());
        }
        return root;
    }

    // --- GameEventListener (pushed updates) ----------------------------------

    @Override
    public void onSceneEntered(Scene scene) {
        renderScene(scene);
    }

    @Override
    public void onCharacterChanged(Character character) {
        renderCharacter(character);
    }

    @Override
    public void onGameOver(Outcome outcome) {
        showGameOver(outcome);
    }

    @Override
    public void onCombatStarted(Npc enemy) {
        combatLog = "Lo scontro con " + enemy.name() + " ha inizio!";
        rebuildChoices();
    }

    @Override
    public void onCombatRound(CombatRound round) {
        combatLog = round.damageTaken() > 0
                ? "Infliggi " + round.damageDealt() + " danni, subisci " + round.damageTaken() + "."
                : "Infliggi " + round.damageDealt() + " danni: il nemico è abbattuto!";
        rebuildChoices();
    }

    @Override
    public void onCombatEnded(boolean playerWon) {
        combatLog = "";
    }

    @Override
    public void onLevelUp(int newLevel) {
        new Alert(Alert.AlertType.INFORMATION, "Sei salito al livello " + newLevel + "!").show();
    }

    // --- rendering -----------------------------------------------------------

    private void renderScene(Scene scene) {
        sceneTitle.setText(scene.title());
        narrative.setText(scene.text());
        renderImage(scene.image());
        rebuildChoices();
    }

    /**
     * Loads the scene illustration from {@code /images/<name>} on the classpath.
     * A scene without a picture, or one whose file is missing, simply shows no
     * image: the slot collapses so it takes up no space.
     */
    private void renderImage(String imageName) {
        Image image = null;
        if (imageName != null && !imageName.isBlank()) {
            var url = getClass().getResource("/images/" + imageName);
            if (url != null) {
                image = new Image(url.toExternalForm());
            }
        }
        sceneImage.setImage(image);
        boolean visible = image != null && !image.isError();
        sceneImage.setVisible(visible);
        sceneImage.setManaged(visible);
    }

    private void rebuildChoices() {
        choiceBox.getChildren().clear();
        saveButton.setDisable(engine.inCombat() || engine.isGameOver());
        if (engine.inCombat()) {
            renderCombat();
            return;
        }
        for (Choice choice : engine.currentChoices()) {
            choiceBox.getChildren().add(choiceButton(choice));
        }
    }

    private void renderCombat() {
        Combat combat = engine.combat();

        Label enemy = new Label(combat.enemyName());
        enemy.getStyleClass().add("enemy-name");
        Label enemyHealth = new Label(
                "Salute nemico: " + combat.enemyHealth() + " / " + combat.enemyMaxHealth());
        enemyHealth.getStyleClass().add("enemy-health");

        choiceBox.getChildren().addAll(enemy, enemyHealth);

        if (!combatLog.isEmpty()) {
            Label log = new Label(combatLog);
            log.setWrapText(true);
            log.getStyleClass().add("combat-log");
            choiceBox.getChildren().add(log);
        }

        Button attack = new Button("Attacca");
        attack.getStyleClass().add("choice-button");
        attack.setMaxWidth(Double.MAX_VALUE);
        attack.setOnAction(e -> engine.attack());
        choiceBox.getChildren().add(attack);

        if (engine.currentScene().encounter().canFlee()) {
            Button flee = new Button("Fuggi");
            flee.getStyleClass().add("choice-button");
            flee.setMaxWidth(Double.MAX_VALUE);
            flee.setOnAction(e -> engine.flee());
            choiceBox.getChildren().add(flee);
        }
    }

    private Button choiceButton(Choice choice) {
        Button button = new Button(choice.text());
        button.getStyleClass().add("choice-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setWrapText(true);
        if (engine.canSelect(choice)) {
            button.setOnAction(e -> engine.choose(choice));
        } else {
            button.setDisable(true);
            String reasons = choice.requirements().stream()
                    .map(r -> r.describe())
                    .collect(Collectors.joining("\n"));
            button.setTooltip(new Tooltip(reasons));
            button.getStyleClass().add("choice-locked");
        }
        return button;
    }

    private void renderCharacter(Character character) {
        characterPanel.getChildren().clear();

        Label name = new Label(character.name());
        name.getStyleClass().add("character-name");

        Label health = new Label("Salute: " + character.health() + " / " + character.maxHealth());
        health.getStyleClass().add("character-health");

        Label level = new Label("Livello " + character.level());
        level.getStyleClass().add("character-level");

        int next = engine.xpForNextLevel();
        String xpText = next > 0
                ? "XP: " + character.xp() + " / " + next
                : "XP: " + character.xp();
        Label xp = new Label(xpText);
        xp.getStyleClass().add("character-xp");

        characterPanel.getChildren().addAll(name, health, level, xp);

        if (!character.attributes().isEmpty()) {
            characterPanel.getChildren().add(sectionTitle("Attributi"));
            character.attributes().forEach((key, value) ->
                    characterPanel.getChildren().add(new Label(key + ": " + value)));
        }

        characterPanel.getChildren().add(sectionTitle("Inventario"));
        if (character.inventory().isEmpty()) {
            Label empty = new Label("(vuoto)");
            empty.getStyleClass().add("muted");
            characterPanel.getChildren().add(empty);
        } else {
            for (Item item : character.inventory()) {
                characterPanel.getChildren().add(new Label("\u2022 " + item.name()));
            }
        }
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    private void showGameOver(Outcome outcome) {
        saveButton.setDisable(true);
        choiceBox.getChildren().clear();

        boolean victory = outcome == Outcome.VICTORY;
        Label result = new Label(victory ? "VITTORIA" : "SCONFITTA");
        result.getStyleClass().add(victory ? "result-victory" : "result-defeat");

        if (!engine.character().isAlive()) {
            narrative.setText("Le tue ferite hanno avuto la meglio. La cripta custodisce un altro avventuriero...");
            sceneTitle.setText("La fine del viaggio");
        }

        Button newGame = new Button("Nuova partita");
        newGame.getStyleClass().add("menu-button");
        newGame.setOnAction(e -> {
            engine.removeListener(this);
            app.showNewGame();
        });

        Button menu = new Button("Torna al menu");
        menu.getStyleClass().add("ghost-button");
        menu.setOnAction(e -> {
            engine.removeListener(this);
            app.showMainMenu();
        });

        HBox buttons = new HBox(12, newGame, menu);
        buttons.setAlignment(Pos.CENTER_LEFT);
        choiceBox.getChildren().addAll(result, buttons);
    }

    private void save() {
        TextInputDialog dialog = new TextInputDialog(engine.character().name());
        dialog.setTitle("Salva partita");
        dialog.setHeaderText(null);
        dialog.setContentText("Nome del salvataggio:");
        Optional<String> name = dialog.showAndWait();
        name.map(String::trim).filter(s -> !s.isEmpty()).ifPresent(saveName -> {
            try {
                gameRepository.save(engine.snapshot(saveName));
                new Alert(Alert.AlertType.INFORMATION, "Partita salvata: " + saveName).showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Salvataggio non riuscito: " + ex.getMessage()).showAndWait();
            }
        });
    }
}
