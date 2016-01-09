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

package com.github.ykrasik.jaci.cli.javafx.commandline;

import com.github.ykrasik.jaci.cli.commandline.CommandLineManager;
import javafx.scene.control.TextField;

import java.util.Objects;

/**
 * An implementation of a {@link CommandLineManager} for JavaFX.
 * Wraps a {@link TextField}.
 *
 * @author Yevgeny Krasik
 */
public class JavaFxCommandLineManager implements CommandLineManager {
    private final TextField textField;

    public JavaFxCommandLineManager(TextField textField) {
        this.textField = Objects.requireNonNull(textField, "textField");
    }

    @Override
    public String getCommandLine() {
        return textField.getText();
    }

    @Override
    public void setCommandLine(String commandLine) {
        textField.setText(commandLine);
    }

    @Override
    public int getCaret() {
        return textField.getCaretPosition();
    }

    @Override
    public void setCaret(int position) {
        textField.positionCaret(position);
    }
}
