/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jerminal.javafx;

import com.github.ykrasik.jerminal.api.CommandLineDriver;
import com.github.ykrasik.jerminal.api.Console;
import com.github.ykrasik.jerminal.api.ConsoleImpl;
import com.github.ykrasik.jerminal.api.Shell;
import com.github.ykrasik.jerminal.api.display.DisplayDriver;
import com.github.ykrasik.jerminal.api.display.terminal.Terminal;
import com.github.ykrasik.jerminal.api.display.terminal.TerminalConfiguration;
import com.github.ykrasik.jerminal.api.display.terminal.TerminalDisplayDriver;
import com.github.ykrasik.jerminal.api.display.terminal.TerminalGuiController;
import com.github.ykrasik.jerminal.api.filesystem.ShellFileSystem;
import com.github.ykrasik.jerminal.javafx.impl.JavaFxCommandLineDriver;
import com.github.ykrasik.jerminal.javafx.impl.JavaFxGuiController;
import com.github.ykrasik.jerminal.javafx.impl.JavaFxTerminal;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * A builder for a JavaFx console.<br>
 * The console is a container for a {@link TextArea} acting as a {@link Terminal}
 * and a {@link TextField} acting as a {@link CommandLineDriver}.<br>
 * <br>
 * Will use a default console layout if built with the default constructor, but can be built with a custom
 * .fxml through other constructors.<br>
 * A custom .fxml must provide 3 nodes:<br>
 *   A {@link TextArea} with id 'terminal'<br>
 *   A {@link TextField} with id 'commandLine'<br>
 *   A {@link Label} with id 'currentPath'<br>
 * <br>
 * The custom .fxml must return a {@link javafx.scene.Parent}.
 *
 * @author Yevgeny Krasik
 */
public class ConsoleBuilder {
    private final ShellFileSystem fileSystem;
    private final FXMLLoader loader;

    private TerminalConfiguration configuration = TerminalConfiguration.DEFAULT_BLACK;
    private String welcomeMessage = "Welcome to Jerminal!\n";
    private int maxCommandHistory = 30;

    /**
     * Constructs a console from the fileSystem using a default layout..
     *
     * @param fileSystem FileSystem to use.
     */
    public ConsoleBuilder(ShellFileSystem fileSystem) {
        this(fileSystem, "/com/github/ykrasik/jerminal/javafx/console.fxml");
    }

    /**
     * Constructs a console from the fileSystem using the .fxml pointed to by the path.
     *
     * @param fileSystem FileSystem to use.
     * @param fxmlPath Path to the .fxml file.
     */
    public ConsoleBuilder(ShellFileSystem fileSystem, String fxmlPath) {
        this(fileSystem, Objects.requireNonNull(ConsoleBuilder.class.getResource(fxmlPath), "Fxml not found on classpath: " + fxmlPath));
    }

    /**
     * Constructs a console from the fileSystem using the .fxml pointed to by the url.
     *
     * @param fileSystem FileSystem to use.
     * @param fxmlUrl URL to the .fxml file.
     */
    public ConsoleBuilder(ShellFileSystem fileSystem, URL fxmlUrl) {
        this.fileSystem = Objects.requireNonNull(fileSystem);
        this.loader = new FXMLLoader(Objects.requireNonNull(fxmlUrl, "Fxml url is null!"));
    }

    /**
     * Builds a {@link Node} that is the console. Will load the .fxml file passed by the constructor.
     * The console can be set toggle-able by attaching a {@link NodeVisibilityToggler} to the {@link javafx.scene.Parent}
     * it is added to.
     *
     * @return A {@link Node} representing the console.
     * @throws IOException If an error loading the .fxml file occurs.
     */
    public Parent build() throws IOException {
        final Parent consoleNode = (Parent) loader.load();

        // Create the terminal.
        final TextArea textArea = (TextArea) consoleNode.lookup("#terminal");
        textArea.setFocusTraversable(false);
        final Terminal terminal = new JavaFxTerminal(textArea);

        // Create the current path label.
        final Label currentPath = (Label) consoleNode.lookup("#currentPath");
        final TerminalGuiController guiController = new JavaFxGuiController(currentPath);

        // Create the shell.
        final DisplayDriver displayDriver = new TerminalDisplayDriver(terminal, guiController, configuration);
        final Shell shell = new Shell(fileSystem, displayDriver, welcomeMessage);

        // Create the command line.
        final TextField commandLine = (TextField) consoleNode.lookup("#commandLine");
        final CommandLineDriver commandLineDriver = new JavaFxCommandLineDriver(commandLine);

        // Create the console.
        final Console console = new ConsoleImpl(shell, commandLineDriver, maxCommandHistory);

        // Hook the commandLine to the console.
        commandLine.requestFocus();
        commandLine.addEventFilter(KeyEvent.KEY_PRESSED, new JavaFxConsoleDriver(console));

        return consoleNode;
    }

    /**
     * @param configuration Configuration to use.
     * @return this, for chained execution.
     */
    public ConsoleBuilder setConfiguration(TerminalConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    /**
     * @param welcomeMessage Welcome message to set.
     * @return this, for chained execution.
     */
    public ConsoleBuilder setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = Objects.requireNonNull(welcomeMessage);
        return this;
    }

    /**
     * @param maxCommandHistory Max command history to set.
     * @return this, for chained execution.
     */
    public ConsoleBuilder setMaxCommandHistory(int maxCommandHistory) {
        this.maxCommandHistory = maxCommandHistory;
        return this;
    }

    /**
     * Links keyEvents to {@link Console} events.
     *
     * @author Yevgeny Krasik
     */
    private static class JavaFxConsoleDriver implements EventHandler<KeyEvent> {
        private final Console console;

        private JavaFxConsoleDriver(Console console) {
            this.console = Objects.requireNonNull(console);
        }

        @Override
        public void handle(KeyEvent keyEvent) {
            switch (keyEvent.getCode()) {
                case ENTER:
                    console.execute();
                    keyEvent.consume();
                    break;

                case TAB:
                    console.assist();
                    keyEvent.consume();
                    break;

                case UP:
                    console.setPrevCommandLineFromHistory();
                    keyEvent.consume();
                    break;

                case DOWN:
                    console.setNextCommandLineFromHistory();
                    keyEvent.consume();
                    break;

                default:
                    break;
            }
        }
    }
}
