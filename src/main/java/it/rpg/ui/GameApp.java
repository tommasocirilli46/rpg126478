package it.rpg.ui;

import it.rpg.engine.GameEngine;
import it.rpg.persistence.GameRepository;
import it.rpg.persistence.JsonGameRepository;
import it.rpg.persistence.JsonStoryRepository;
import it.rpg.persistence.StoryRepository;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The JavaFX application. It wires the concrete repositories, owns the single
 * window and swaps the visible view. The views depend only on the {@code
 * GameEngine} and repository abstractions, never on each other's internals.
 */
public final class GameApp extends Application {

    private final StoryRepository storyRepository = new JsonStoryRepository();
    private final GameRepository gameRepository = new JsonGameRepository();

    private Stage stage;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Narrative RPG \u2014 La Cripta Dimenticata");
        stage.setMinWidth(820);
        stage.setMinHeight(560);
        showMainMenu();
        stage.show();
    }

    // --- navigation ----------------------------------------------------------

    void showMainMenu() {
        setRoot(new MainMenuView(this).view());
    }

    void showNewGame() {
        setRoot(new NewGameView(this, storyRepository).view());
    }

    void showLoadGame() {
        setRoot(new LoadGameView(this, gameRepository, storyRepository).view());
    }

    void showGame(GameEngine engine) {
        setRoot(new GameView(this, engine, gameRepository).view());
    }

    private void setRoot(Parent root) {
        if (scene == null) {
            scene = new Scene(root, 920, 620);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
    }
}
