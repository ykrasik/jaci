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

package com.github.ykrasik.jerminal.libgdx.impl;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.ykrasik.jerminal.api.CommandLineDriver;

import java.util.Objects;

/**
 * An implementation of a {@link CommandLineDriver} for LibGdx.<br>
 * Wraps a {@link TextField}.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxCommandLineDriver implements CommandLineDriver {
    private final TextField textField;

    public LibGdxCommandLineDriver(TextField textField) {
        this.textField = Objects.requireNonNull(textField);
    }

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
