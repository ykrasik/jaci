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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.ykrasik.jemi.cli.output.CliOutput;
import lombok.NonNull;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class LibGdxCliOutput implements CliOutput {
    private final LibGdxCliOutputBuffer buffer;
    private final TextField commandLine;
    private final Label workingDirectory;

    public LibGdxCliOutput(@NonNull LibGdxCliOutputBuffer buffer,
                           @NonNull TextField commandLine,
                           @NonNull Label workingDirectory) {
        this.buffer = buffer;
        this.commandLine = commandLine;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void begin() {
        // Nothing to do here.
    }

    @Override
    public void end() {
        // Nothing to do here.
    }

    @Override
    public void println(String text) {
        buffer.println(text, Color.WHITE);
    }

    @Override
    public void errorPrintln(String text) {
        buffer.println(text, Color.PINK);
    }

    @Override
    public void setCommandLine(String commandLine) {
        this.commandLine.setText(commandLine);
    }

    @Override
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory.setText(workingDirectory);
    }
}
