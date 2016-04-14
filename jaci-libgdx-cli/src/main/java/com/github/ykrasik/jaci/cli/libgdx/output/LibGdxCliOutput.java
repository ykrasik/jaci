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

package com.github.ykrasik.jaci.cli.libgdx.output;

import com.badlogic.gdx.graphics.Color;
import com.github.ykrasik.jaci.cli.output.CliOutput;

import java.util.Objects;

/**
 * A {@link CliOutput} that writes to a {@link LibGdxCliOutputBuffer}
 * with all text colored in a specific {@link Color}.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxCliOutput implements CliOutput {
    private final LibGdxCliOutputBuffer buffer;
    private final Color color;

    public LibGdxCliOutput(LibGdxCliOutputBuffer buffer, Color color) {
        this.buffer = Objects.requireNonNull(buffer);
        this.color = Objects.requireNonNull(color);
    }

    @Override
    public void println(String text) {
        buffer.println(text, color);
    }
}
