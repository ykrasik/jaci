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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.ykrasik.jerminal.api.Shell;
import com.github.ykrasik.jerminal.api.commandline.CommandLineDriver;
import com.github.ykrasik.jerminal.api.commandline.ShellWithCommandLine;
import com.github.ykrasik.jerminal.api.commandline.ShellWithCommandLineImpl;

/**
 * @author Yevgeny Krasik
 */
// FIXME: JavaDoc
public class LibGdxConsole extends Table {
    private final ShellWithCommandLine shell;
    private final ConsoleToggler consoleToggler;

    private final Label currentPath;
    private final TextField textField;
    private final Table terminalScreen;

    private ConsoleActivationListener activationListener;
    private Actor prevKeyboardFocus;

    public LibGdxConsole(LibGdxTerminal terminal,
                         Shell shell,
                         ConsoleToggler consoleToggler,
                         LibGdxConsoleWidgetFactory widgetFactory) {
        this.shell = new ShellWithCommandLineImpl(shell, new LibGdxCommandLineDriver());
        this.consoleToggler = consoleToggler;

        // TextField to input commands.
        textField = widgetFactory.createInputTextField("");
        textField.setName("textField");

        terminal.bottom().left();
        terminal.debug();

        // A "current-path" thing.
        currentPath = widgetFactory.createCurrentPathLabel("$");
        currentPath.setName("currentPathLabel");
        final Table currentPathTable = new Table();
        currentPathTable.setName("currentPath");
        currentPathTable.add(currentPath).fill().padLeft(3).padRight(5);
        currentPathTable.setBackground(widgetFactory.createCurrentPathLabelBackground());
        currentPathTable.debug();

        // A close console button.
        final Button closeButton = widgetFactory.createCloseButton();
        closeButton.setName("closeButton");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deactivate();
            }
        });

        // The table that will contain the bottom row - current path, text input, close button.
        final Table bottomRow = new Table();
        bottomRow.setName("bottomRow");
        bottomRow.setBackground(widgetFactory.createConsoleBottomRowBackground());
        bottomRow.add(currentPathTable).fill();
        bottomRow.add(textField).fill().expandX();
        bottomRow.add(closeButton).fill().width(closeButton.getWidth());
        bottomRow.debug();

        terminalScreen = new Table();
        terminalScreen.setName("terminalScreen");
        terminalScreen.setBackground(widgetFactory.createTerminalBufferBackground());
        terminalScreen.pad(0);
        terminalScreen.add(terminal).fill().expand();
        terminalScreen.row();
        terminalScreen.add(bottomRow).fill();

        terminalScreen.top().left();
        terminalScreen.setFillParent(true);
        terminalScreen.debug();

        add(terminalScreen);
        addListener(new ConsoleInputListener());

        bottom().left();

        setVisible(false);
    }

    @Override
    protected void setStage(Stage stage) {
        if (stage != null) {
            stage.addListener(new ConsoleToggleListener());
        }
        super.setStage(stage);
    }

    public void setConsoleActivationListener(ConsoleActivationListener listener) {
        this.activationListener = listener;
    }

    public boolean isActive() {
        return isVisible();
    }

    public void activate() {
        if (isActive()) {
            return;
        }

        setVisible(true);

        // Set keyboard focus to the textField, preserving the original keyboardFocus.
        final Stage stage = getStage();
        if (stage != null) {
            prevKeyboardFocus = stage.getKeyboardFocus();
            stage.setKeyboardFocus(textField);
        }

        // Notify activationListener.
        if (activationListener != null) {
            activationListener.activated();
        }
    }

    public void deactivate() {
        if (!isActive()) {
            return;
        }

        setVisible(false);

        // Restore the original keyboardFocus.
        final Stage stage = getStage();
        if (stage != null) {
            stage.setKeyboardFocus(prevKeyboardFocus);
        }

        // Notify activationListener.
        if (activationListener != null) {
            activationListener.deactivated();
        }
    }

    public void toggle() {
        if (!isActive()) {
            activate();
        } else {
            deactivate();
        }
    }

    private class ConsoleToggleListener extends InputListener {
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (consoleToggler.shouldToggle(keycode)) {
                toggle();
                event.cancel();
                return true;
            }
            return false;
        }
    }

    private class LibGdxCommandLineDriver implements CommandLineDriver {
        @Override
        public String read() {
            return textField.getText();
        }

        @Override
        public String readUntilCaret() {
            return textField.getText().substring(0, textField.getCursorPosition());
        }

        @Override
        public void set(String commandLine) {
            textField.setText(commandLine);
            textField.setCursorPosition(commandLine.length());
        }

        @Override
        public void clear() {
            set("");
        }
    }

    private class ConsoleInputListener extends InputListener {
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (!isActive()) {
                // Shouldn't be called, but just in case.
                return false;
            }

            switch (keycode) {
                case Keys.DPAD_UP: shell.setPrevCommandLineFromHistory(); return true;
                case Keys.DPAD_DOWN: shell.setNextCommandLineFromHistory(); return true;
                case Keys.ENTER: shell.execute(); return true;
                case Keys.TAB: shell.assist(); return true;
                case Keys.Z:
                    if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
                        shell.clearCommandLine();
                        return true;
                    }
            }

            return false;
        }
    }
}
