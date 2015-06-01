/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jemi.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.ykrasik.jemi.cli.CliShell;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Console;

/**
 * Links input events to {@link Console} events.
 *
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@RequiredArgsConstructor
public class LibGdxCliInputListener extends InputListener {
    @NonNull private final CliShell shell;
    @NonNull private final TextField commandLineTextField;

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        switch (keycode) {
            case Keys.ENTER:
                execute();
                return true;

            case Keys.TAB:
                assist();
                setCaretToEnd();
                return true;

            case Keys.DPAD_UP:
                shell.setPrevCommandLineFromHistory();
                setCaretToEnd();
                return true;

            case Keys.DPAD_DOWN:
                shell.setNextCommandLineFromHistory();
                setCaretToEnd();
                return true;

            case Keys.Z:
                if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
                    commandLineTextField.setText("");
                    return true;
                }
        }

        return false;
    }

    private void execute() {
        final String commandLine = read();
        shell.execute(commandLine);
    }

    private void assist() {
        final String commandLine = readUntilCaret();
        shell.assist(commandLine);
    }

    private String read() {
        return commandLineTextField.getText();
    }

    private String readUntilCaret() {
        return read().substring(0, commandLineTextField.getCursorPosition());
    }

    private void setCaretToEnd() {
        commandLineTextField.setCursorPosition(commandLineTextField.getText().length());
    }
}
