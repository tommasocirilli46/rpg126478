package it.rpg;

import it.rpg.ui.GameApp;
import javafx.application.Application;

/**
 * Application entry point.
 *
 * <p>Deliberately a plain class that does <em>not</em> extend
 * {@link javafx.application.Application}. Launching the JavaFX app from a
 * separate class lets it start from the classpath without a
 * {@code module-info.java}, avoiding the "JavaFX runtime components are
 * missing" error.</p>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Application.launch(GameApp.class, args);
    }
}
