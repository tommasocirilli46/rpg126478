package it.rpg.ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** The landing screen: start a new game, load a save, or quit. */
final class MainMenuView {

    private final GameApp app;

    MainMenuView(GameApp app) {
        this.app = app;
    }

    Parent view() {
        Label title = new Label("La Cripta Dimenticata");
        title.getStyleClass().add("game-title");

        Label subtitle = new Label("Un'avventura testuale a scelte");
        subtitle.getStyleClass().add("subtitle");

        Button newGame = menuButton("Nuova Partita", app::showNewGame);
        Button loadGame = menuButton("Carica Partita", app::showLoadGame);
        Button quit = menuButton("Esci", Platform::exit);

        VBox box = new VBox(14, title, subtitle, newGame, loadGame, quit);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("menu");
        return box;
    }

    private Button menuButton(String text, Runnable action) {
        Button b = new Button(text);
        b.getStyleClass().add("menu-button");
        b.setMaxWidth(260);
        b.setOnAction(e -> action.run());
        return b;
    }
}
