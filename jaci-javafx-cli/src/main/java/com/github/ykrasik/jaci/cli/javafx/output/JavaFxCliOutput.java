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

package com.github.ykrasik.jaci.cli.javafx.output;

import com.github.ykrasik.jaci.cli.output.CliOutput;
import javafx.scene.control.TextArea;

import java.util.Objects;

/**
 * A {@link CliOutput} that writes to a JavaFx {@link TextArea}.
 *
 * @author Yevgeny Krasik
 */
public class JavaFxCliOutput implements CliOutput {
    private final TextArea textArea;

    public JavaFxCliOutput(TextArea textArea) {
        this.textArea = Objects.requireNonNull(textArea, "textArea");
    }

    @Override
    public void println(String text) {
        textArea.appendText(text);
        textArea.appendText("\n");
    }
}
