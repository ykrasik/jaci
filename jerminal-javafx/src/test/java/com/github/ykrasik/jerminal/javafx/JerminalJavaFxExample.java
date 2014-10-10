package com.github.ykrasik.jerminal.javafx;

import com.github.ykrasik.jerminal.api.output.terminal.Terminal;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;

/**
 * @author Yevgeny Krasik
 */
public class JerminalJavaFxExample extends Application {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            doStart(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() throws Exception {
        executor.shutdownNow();
    }

    private void doStart(Stage stage) throws IOException {
        final BorderPane mainWindow = (BorderPane) loadFxml("main.fxml");
        this.scene = new Scene(mainWindow);

        stage.setTitle("Jerminal");
        stage.setScene(scene);
        stage.show();

        final TextArea textArea = findById("textArea", TextArea.class);
        final Terminal terminal = new JavaFxTerminal(textArea);

        final TextField textField = findById("textField", TextField.class);
        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case ENTER:
                        final String commandLine = textField.getText();
                    case TAB:
                    case UP:
                    case DOWN:
                    default:

                }
            }
        });
    }

    private Object loadFxml(String path) throws IOException {
        final URL resource = Objects.requireNonNull(getClass().getResource(path));
        final FXMLLoader loader = new FXMLLoader(resource);
        return loader.load();
    }

    private <T> T findById(String id, Class<T> clazz) {
        return clazz.cast(findById(id));
    }

    private Node findById(String id) {
        return scene.lookup('#' + id);
    }
}
