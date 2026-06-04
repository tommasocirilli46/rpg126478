package it.rpg.ui;

import it.rpg.engine.GameEngine;
import it.rpg.engine.StoryGameEngine;
import it.rpg.model.GameState;
import it.rpg.model.StoryGraph;
import it.rpg.persistence.GameRepository;
import it.rpg.persistence.SaveInfo;
import it.rpg.persistence.StoryRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/** Shows existing saves and lets the player resume or delete one. */
final class LoadGameView {

    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    private final GameApp app;
    private final GameRepository gameRepository;
    private final StoryRepository storyRepository;
    private final ListView<SaveInfo> list = new ListView<>();

    LoadGameView(GameApp app, GameRepository gameRepository, StoryRepository storyRepository) {
        this.app = app;
        this.gameRepository = gameRepository;
        this.storyRepository = storyRepository;
    }

    Parent view() {
        Label heading = new Label("Carica Partita");
        heading.getStyleClass().add("heading");

        list.setCellFactory(v -> saveCell());
        list.setPlaceholder(new Label("Nessun salvataggio disponibile."));
        VBox.setVgrow(list, Priority.ALWAYS);
        refresh();

        Button load = new Button("Carica");
        load.getStyleClass().add("menu-button");
        load.setOnAction(e -> loadSelected());

        Button delete = new Button("Elimina");
        delete.getStyleClass().add("ghost-button");
        delete.setOnAction(e -> deleteSelected());

        Button back = new Button("Indietro");
        back.getStyleClass().add("ghost-button");
        back.setOnAction(e -> app.showMainMenu());

        HBox actions = new HBox(12, back, delete, load);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox box = new VBox(14, heading, list, actions);
        box.setPadding(new Insets(28));
        box.getStyleClass().add("panel");
        return box;
    }

    private void refresh() {
        list.getItems().setAll(gameRepository.listSaves());
    }

    private void loadSelected() {
        SaveInfo selected = list.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        try {
            Optional<GameState> state = gameRepository.load(selected.saveName());
            if (state.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Salvataggio non trovato.").showAndWait();
                return;
            }
            StoryGraph story = storyRepository.load(state.get().storyId());
            GameEngine engine = new StoryGameEngine(story);
            engine.restore(state.get());
            app.showGame(engine);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Impossibile caricare: " + ex.getMessage()).showAndWait();
        }
    }

    private void deleteSelected() {
        SaveInfo selected = list.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Eliminare il salvataggio \"" + selected.saveName() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b -> {
            gameRepository.delete(selected.saveName());
            refresh();
        });
    }

    private ListCell<SaveInfo> saveCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(SaveInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.saveName() + "    \u2014    " + STAMP.format(Instant.ofEpochMilli(item.savedAt())));
                }
            }
        };
    }
}
