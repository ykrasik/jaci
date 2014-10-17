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

package com.github.ykrasik.jerminal.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.ykrasik.jerminal.api.Console;
import com.github.ykrasik.jerminal.api.ConsoleImpl;
import com.github.ykrasik.jerminal.api.Shell;
import com.github.ykrasik.jerminal.api.display.DisplayDriver;
import com.github.ykrasik.jerminal.api.display.terminal.TerminalGuiController;
import com.github.ykrasik.jerminal.api.filesystem.ShellFileSystem;
import com.github.ykrasik.jerminal.libgdx.impl.*;

import java.util.Objects;

/**
 * A builder for a {@link LibGdxConsole}.<br>
 *
 * @author Yevgeny Krasik
 */
// FIXME: Explain the toggle functionality.
public class ConsoleBuilder {
    private final ShellFileSystem fileSystem;
    private final Skin skin;

    private int maxTerminalEntries = 30;
    private int maxCommandHistory = 30;
    private String welcomeMessage = "Welcome to Jerminal!\n";

    public ConsoleBuilder(ShellFileSystem fileSystem) {
        this(fileSystem, new Skin(Gdx.files.classpath("com/github/ykrasik/jerminal/libgdx/console.cfg")));
    }

    public ConsoleBuilder(ShellFileSystem fileSystem, Skin skin) {
        this.fileSystem = Objects.requireNonNull(fileSystem);
        this.skin = Objects.requireNonNull(skin);
    }

    public LibGdxConsole build() {
        // Create the terminal.
        final LibGdxTerminal terminal = new LibGdxTerminal(skin, maxTerminalEntries);
        terminal.setName("terminal");
        terminal.bottom().left();

        // Create the current path label.
        final Label currentPath = new Label("", skin, "currentPath");
        currentPath.setName("currentPath");
        final TerminalGuiController guiController = new LibGdxTerminalGuiController(currentPath);

        // Create the shell.
        final DisplayDriver displayDriver = new LibGdxTerminalDisplayDriver(terminal, guiController, new LibGdxTerminalSerializer());
        final Shell shell = new Shell(fileSystem, displayDriver, welcomeMessage);

        // Create the command line.
        final TextField commandLine = new TextField("", skin, "commandLine");
        commandLine.setName("commandLine");
        final LibGdxCommandLineDriver commandLineDriver = new LibGdxCommandLineDriver(commandLine);

        // Create the console.
        final Console console = new ConsoleImpl(shell, commandLineDriver, maxCommandHistory);

        // Hook the commandLine to the console.
        commandLine.addListener(new LibGdxConsoleDriver(console));

        return createConsoleTable(terminal, currentPath, commandLine);
    }

    private LibGdxConsole createConsoleTable(LibGdxTerminal terminal, Label currentPath, final TextField commandLine) {
        final LibGdxConsole consoleTable = new LibGdxConsole(skin);
        consoleTable.setName("consoleTable");
        consoleTable.setBackground("consoleBackground");
        consoleTable.addVisibleListener(new VisibleListener() {
            @Override
            public void onVisibleChange(boolean wasVisible, boolean isVisible) {
                if (!wasVisible && isVisible) {
                    final Stage stage = consoleTable.getStage();
                    if (stage != null) {
                        stage.setKeyboardFocus(commandLine);
                    }
                }
            }
        });

        // A close console button.
        final Button closeButton = new TextButton("X", skin, "closeConsoleButton");
        closeButton.padRight(15).padLeft(15);
        closeButton.setName("closeButton");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                consoleTable.setVisible(false);
            }
        });

        // Some layout.

        final Table currentPathTable = new Table(skin);
        currentPathTable.setName("currentPathTable");
        currentPathTable.setBackground("currentPathBackground");
        currentPathTable.add(currentPath).fill().padLeft(3).padRight(5);

        // The bottom row contains the current path, command line and a close button.
        final Table bottomRow = new Table(skin);
        bottomRow.setName("bottomRow");
        bottomRow.setBackground("bottomRowBackground");
        bottomRow.add(currentPathTable).fill();
        bottomRow.add(commandLine).fill().expandX();
        bottomRow.add(closeButton).fill();

        consoleTable.pad(0);
        consoleTable.add(terminal).fill().expand();
        consoleTable.row();
        consoleTable.add(bottomRow).fill();
        consoleTable.top().left();

        return consoleTable;
    }

    public ConsoleBuilder setMaxTerminalEntries(int maxTerminalEntries) {
        this.maxTerminalEntries = maxTerminalEntries;
        return this;
    }

    public ConsoleBuilder setMaxCommandHistory(int maxCommandHistory) {
        this.maxCommandHistory = maxCommandHistory;
        return this;
    }

    public ConsoleBuilder setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = Objects.requireNonNull(welcomeMessage);
        return this;
    }

    /**
     * Links input events to {@link com.github.ykrasik.jerminal.api.Console} events.
     *
     * @author Yevgeny Krasik
     */
    private static class LibGdxConsoleDriver extends InputListener {
        private final Console console;

        private LibGdxConsoleDriver(Console console) {
            this.console = Objects.requireNonNull(console);
        }

        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            switch (keycode) {
                case Keys.ENTER:
                    console.execute();
                    return true;

                case Keys.TAB:
                    console.assist();
                    return true;

                case Keys.DPAD_UP:
                    console.setPrevCommandLineFromHistory();
                    return true;

                case Keys.DPAD_DOWN:
                    console.setNextCommandLineFromHistory();
                    return true;

                case Keys.Z:
                    if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
                        console.clearCommandLine();
                        return true;
                    }
            }

            return false;
        }
    }
}
