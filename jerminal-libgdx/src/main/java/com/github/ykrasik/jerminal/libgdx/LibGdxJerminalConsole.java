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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.ykrasik.jerminal.api.Shell;
import com.google.common.base.Optional;

/**
 * A Jerminal console implementation based on libGdx (http://libgdx.badlogicgames.com/).<br>
 * The console is implemented as a {@link Stage} that uses a {@link LibGdxTerminal} as a display.<br>
 * <br>
 * <p>The console is activated/deactivated by a configurable toggle key. A {@link ConsoleListener} can
 * be set to be called every time the console is activated/deactivated.</p><br>
 * <br>
 * <p>In order to work, the console must be hooked into an application's main loop and receive
 * {@link #draw()} and {@link #act(float)} calls.</p>
 *
 * @author Yevgeny Krasik
 */
// TODO: Implement coloring suggestions in yellow.
    // FIXME: This should be a table.
public class LibGdxJerminalConsole extends Stage {
    private final Shell shell;
    private final int toggleKeycode;

    private final Label currentPath;
    private final TextField textField;
    private final Table terminalScreen;

    private ConsoleListener listener;
    private boolean active;
    private boolean needsKeyboardFocus;

    public LibGdxJerminalConsole(LibGdxTerminal terminal,
                                 Shell shell,
                                 LibGdxConsoleWidgetFactory widgetFactory,
                                 int toggleKeycode,
                                 float width,
                                 float height) {
        super(width, height, true);
        this.shell = shell;
        this.toggleKeycode = toggleKeycode;

        terminal.bottom().left();
        terminal.debug();

        // A "current-path" thing.
        currentPath = widgetFactory.createCurrentPathLabel("$");
        final Table currentPathTable = new Table();
        currentPathTable.add(currentPath).fill().padLeft(3).padRight(5);
        currentPathTable.setBackground(widgetFactory.createCurrentPathLabelBackground());
        currentPathTable.debug();

        // TextField to input commands.
        textField = widgetFactory.createInputTextField("");

        // A close console button.
        final Button closeButton = widgetFactory.createCloseButton();
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deactivate();
            }
        });

        // The table that will contain the bottom row - current path, text input, close button.
        final Table bottomRow = new Table();
        bottomRow.setBackground(widgetFactory.createConsoleBottomRowBackground());
        bottomRow.add(currentPathTable).fill();
        bottomRow.add(textField).fill().expandX();
        bottomRow.add(closeButton).fill().width(closeButton.getWidth());
        bottomRow.debug();

        terminalScreen = new Table();
        terminalScreen.setBackground(widgetFactory.createTerminalBufferBackground());
        terminalScreen.pad(0);
        terminalScreen.add(terminal).fill().expand();
        terminalScreen.row();
        terminalScreen.add(bottomRow).fill();

        terminalScreen.top().left();
        terminalScreen.setFillParent(true);
        terminalScreen.setVisible(false);

        terminalScreen.setName("terminalScreen");
        terminalScreen.debug();

        this.addActor(terminalScreen);
    }

    public void setListener(ConsoleListener listener) {
        this.listener = listener;
    }

    public void activate() {
        if (active) {
            return;
        }

        active = true;
        terminalScreen.setVisible(true);

        // Delegate giving the textField keyboard focus so that the key that activated
        // the terminal doesn't get typed into the textField.
        needsKeyboardFocus = true;

        // Notify listener.
        if (listener != null) {
            listener.activated();
        }
    }

    public void deactivate() {
        if (!active) {
            return;
        }

        active = false;
        terminalScreen.setVisible(false);

        // Clear stage keyboard focus.
        this.setKeyboardFocus(null);

        // Notify listener.
        if (listener != null) {
            listener.deactivated();
        }
    }

    public void toggle() {
        if (!active) {
            activate();
        } else {
            deactivate();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == toggleKeycode) {
            toggle();
            return true;
        }

        if (!active) {
            return false;
        }

        switch (keycode) {
            case Keys.DPAD_UP:
                showCommandLineIfPresent(shell.getPrevCommandLineFromHistory());
                return true;
            case Keys.DPAD_DOWN:
                showCommandLineIfPresent(shell.getNextCommandLineFromHistory());
                return true;
            case Keys.ENTER:
                executeCommandLine();
                return true;
            case Keys.TAB:
                assistCommandLine();
                return true;
            case Keys.Z:
                if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
                    // Clear command line.
                    setCommandLine("");
                    return true;
                }
        }

        return super.keyDown(keycode);
    }

    private void showCommandLineIfPresent(Optional<String> commandLine) {
        if (commandLine.isPresent()) {
            setCommandLine(commandLine.get());
        }
    }

    private void executeCommandLine() {
        final String commandLine = readCommandLine();
        final String newCommandLine = shell.execute(commandLine);
        setCommandLine(newCommandLine);
    }

    private void assistCommandLine() {
        final String commandLine = readCommandLineUntilCursor();
        final String newCommandLine = shell.assist(commandLine);
        setCommandLine(newCommandLine);
    }

    private String readCommandLine() {
        return textField.getText();
    }

    private String readCommandLineUntilCursor() {
        return textField.getText().substring(0, textField.getCursorPosition());
    }

    private void setCommandLine(String commandLine) {
        textField.setText(commandLine);
        textField.setCursorPosition(commandLine.length());
    }

    @Override
    public boolean keyUp(int keycode) {
        return active && super.keyUp(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        return active && super.keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return active && super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return active && super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return active && super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return active && super.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(int amount) {
        return active && super.scrolled(amount);
    }

    @Override
    public void draw() {
        if (active) {
            if (needsKeyboardFocus) {
                this.setKeyboardFocus(textField);
                needsKeyboardFocus = false;
            }

            super.draw();
        }
    }

    @Override
    public void act(float delta) {
        if (active) {
            super.act(delta);
        }
    }

    //    @Override
//    public void setCurrentPath(List<String> path) {
//        final StringBuffer sb = new StringBuffer();
//        for (String entry : path) {
//            sb.append(entry);
//            sb.append('/');
//        }
//        if (sb.length() != 0) {
//            sb.replace(sb.length() - 1, sb.length(), " ");
//        }
//        sb.append('$');
//        currentPath.setText(sb.toString());
//    }
}
