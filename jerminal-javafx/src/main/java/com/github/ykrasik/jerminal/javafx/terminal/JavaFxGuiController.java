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

package com.github.ykrasik.jerminal.javafx.terminal;

import com.github.ykrasik.jerminal.api.display.terminal.DefaultTerminalGuiController;
import javafx.scene.control.Label;

import java.util.Objects;

/**
 * A specialized version of a {@link DefaultTerminalGuiController} for JavaFx.
 * Wraps the 'current path' {@link Label}.
 *
 * @author Yevgeny Krasik
 */
public class JavaFxGuiController extends DefaultTerminalGuiController {
    private final Label currentPathLabel;

    public JavaFxGuiController(Label currentPathLabel) {
        this.currentPathLabel = Objects.requireNonNull(currentPathLabel);
    }

    @Override
    protected void doSetWorkingDirectory(String path) {
        currentPathLabel.setText(path);
    }
}
