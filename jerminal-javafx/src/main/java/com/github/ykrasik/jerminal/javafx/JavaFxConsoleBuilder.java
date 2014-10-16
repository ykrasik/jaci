/*
* Copyright (C) 2014 Yevgeny Krasik
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.github.ykrasik.jerminal.javafx;

import com.github.ykrasik.jerminal.api.CommandLineDriver;
import com.github.ykrasik.jerminal.api.Console;
import com.github.ykrasik.jerminal.api.ConsoleImpl;
import com.github.ykrasik.jerminal.api.Shell;
import com.github.ykrasik.jerminal.api.display.DisplayDriver;
import com.github.ykrasik.jerminal.api.display.terminal.Terminal;
import com.github.ykrasik.jerminal.api.display.terminal.TerminalDisplayDriver;
import com.github.ykrasik.jerminal.api.display.terminal.TerminalGuiController;
import com.github.ykrasik.jerminal.api.filesystem.ShellFileSystem;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * A builder for a JavaFx console.<br>
 * The console is a {@link BorderPane} that contains a {@link TextArea} acting as a {@link Terminal}
 * and a {@link TextField} acting as a {@link CommandLineDriver}.
 *
 * @author Yevgeny Krasik
 */
// FIXME: JavaDoc
public class JavaFxConsoleBuilder {
    // TODO: Add some sort of toggler?
    private final ShellFileSystem fileSystem;

    private String welcomeMessage = "Welcome to Jerminal!\n";
    private int maxCommandHistory = 30;

    public JavaFxConsoleBuilder(ShellFileSystem fileSystem) {
        this.fileSystem = Objects.requireNonNull(fileSystem);
    }

    // FIXME: JavaDoc
    public BorderPane build() throws IOException {
        final BorderPane borderPane = (BorderPane) loadFxml("/com/github/ykrasik/jerminal/javafx/main.fxml");

        // Create the terminal.
        final TextArea textArea = (TextArea) borderPane.lookup("#textArea");
        textArea.setFocusTraversable(false);
        final Terminal terminal = new JavaFxTerminal(textArea);

        // Create the current path label.
        final Label currentPathLabel = (Label) borderPane.lookup("#currentPathLabel");
        final TerminalGuiController guiController = new JavaFxGuiController(currentPathLabel);

        // Create the shell.
        final DisplayDriver displayDriver = new TerminalDisplayDriver(terminal, guiController);
        final Shell shell = new Shell(fileSystem, displayDriver, welcomeMessage);

        // Create the command line.
        final TextField textField = (TextField) borderPane.lookup("#textField");
        final CommandLineDriver commandLineDriver = new JavaFxCommandLineDriver(textField);

        // Create the console.
        final Console console = new ConsoleImpl(shell, commandLineDriver, maxCommandHistory);

        // Hook the textField to the console.
        textField.requestFocus();
        textField.addEventFilter(KeyEvent.KEY_PRESSED, new JavaFxConsoleDriver(console));

        return borderPane;
    }

    public JavaFxConsoleBuilder setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = Objects.requireNonNull(welcomeMessage);
        return this;
    }

    public JavaFxConsoleBuilder setMaxCommandHistory(int maxCommandHistory) {
        this.maxCommandHistory = maxCommandHistory;
        return this;
    }

    private Object loadFxml(String path) throws IOException {
        final URL resource = JavaFxConsoleBuilder.class.getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Resource not found: " + path);
        }
        final FXMLLoader loader = new FXMLLoader(resource);
        return loader.load();
    }
}
