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

package com.github.ykrasik.jaci.cli.libgdx.commandline;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.ykrasik.jaci.cli.commandline.CommandLineManager;

import java.util.Objects;

/**
 * A LibGdx implementation of a {@link CommandLineManager}.
 * Wraps a {@link TextField} which it uses as a command-line.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxCommandLineManager implements CommandLineManager {
    private final TextField commandLine;

    /**
     * @param commandLine TextField to use as a command-line.
     */
    public LibGdxCommandLineManager(TextField commandLine) {
        this.commandLine = Objects.requireNonNull(commandLine, "commandLine");
    }

    @Override
    public String getCommandLine() {
        return commandLine.getText();
    }

    @Override
    public void setCommandLine(String commandLine) {
        this.commandLine.setText(commandLine);
    }

    @Override
    public int getCaret() {
        return commandLine.getCursorPosition();
    }

    @Override
    public void setCaret(int position) {
        this.commandLine.setCursorPosition(position);
    }
}
