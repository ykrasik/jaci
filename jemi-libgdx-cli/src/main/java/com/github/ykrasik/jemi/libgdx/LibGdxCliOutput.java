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
import com.github.ykrasik.jemi.cli.output.CliOutput;
import lombok.NonNull;

/**
 * A LibGdx implementation of a {@link CliOutput}.
 * Redirects {@link #println(String)} and {@link #errorPrintln(String)} to a {@link LibGdxCliOutputBuffer},
 * and {@link #setWorkingDirectory(String)} to a {@link Label}.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxCliOutput implements CliOutput {
    private final LibGdxCliOutputBuffer buffer;
    private final Label workingDirectory;

    public LibGdxCliOutput(@NonNull LibGdxCliOutputBuffer buffer, @NonNull Label workingDirectory) {
        this.buffer = buffer;
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
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory.setText(workingDirectory);
    }
}
