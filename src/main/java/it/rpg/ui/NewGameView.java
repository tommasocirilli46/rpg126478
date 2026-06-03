package it.rpg.ui;

import it.rpg.engine.GameEngine;
import it.rpg.engine.StoryGameEngine;
import it.rpg.model.StoryGraph;
import it.rpg.persistence.StoryInfo;
import it.rpg.persistence.StoryRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/** Lets the player pick a hero name and a story before starting. */
final class NewGameView {

    private final GameApp app;
    private final StoryRepository storyRepository;

    NewGameView(GameApp app, StoryRepository storyRepository) {
        this.app = app;
        this.storyRepository = storyRepository;
    }

    Parent view() {
        Label heading = new Label("Nuova Partita");
        heading.getStyleClass().add("heading");

        TextField nameField = new TextField();
        nameField.setPromptText("Nome dell'eroe");
        nameField.setMaxWidth(320);

        ComboBox<StoryInfo> storyBox = new ComboBox<>();
        storyBox.setMaxWidth(320);
        storyBox.setCellFactory(list -> storyCell());
        storyBox.setButtonCell(storyCell());

        Label description = new Label();
        description.getStyleClass().add("description");
        description.setWrapText(true);
        description.setMaxWidth(360);

        List<StoryInfo> stories = storyRepository.listAvailable();
        storyBox.getItems().setAll(stories);
        if (!stories.isEmpty()) {
            storyBox.getSelectionModel().selectFirst();
            description.setText(stories.get(0).description());
        }
        storyBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> description.setText(sel == null ? "" : sel.description()));

        Button start = new Button("Inizia l'avventura");
        start.getStyleClass().add("menu-button");
        start.setOnAction(e -> startGame(nameField, storyBox));

        Button back = new Button("Indietro");
        back.getStyleClass().add("ghost-button");
        back.setOnAction(e -> app.showMainMenu());

        HBox actions = new HBox(12, back, start);
        actions.setAlignment(Pos.CENTER);

        VBox box = new VBox(14, heading,
                new Label("Come ti chiami?"), nameField,
                new Label("Quale storia vuoi vivere?"), storyBox, description,
                actions);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(28));
        box.getStyleClass().add("panel");
        return box;
    }

    private void startGame(TextField nameField, ComboBox<StoryInfo> storyBox) {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        StoryInfo selected = storyBox.getSelectionModel().getSelectedItem();
        if (name.isEmpty()) {
            name = "Avventuriero";
        }
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Seleziona una storia.").showAndWait();
            return;
        }
        try {
            StoryGraph story = storyRepository.load(selected.id());
            GameEngine engine = new StoryGameEngine(story);
            engine.startNewGame(name);
            app.showGame(engine);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Impossibile avviare la partita: " + ex.getMessage()).showAndWait();
        }
    }

    private ListCell<StoryInfo> storyCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(StoryInfo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.title());
            }
        };
    }
}
