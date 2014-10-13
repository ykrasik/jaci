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
import com.github.ykrasik.jerminal.api.filesystem.ShellFileSystem;

import java.util.Objects;

/**
 * A builder for a {@link LibGdxConsole}.<br>
 *
 * @author Yevgeny Krasik
 */
// FIXME: Explain the toggle functionality.
// FIXME: I'm not sure this class is helpful.
public class LibGdxConsoleBuilder {
    private static final ConsoleToggler DEFAULT_CONSOLE_TOGGLER = new ConsoleToggler() {
        @Override
        public boolean shouldToggle(int keycode) {
            return keycode == Keys.GRAVE &&
                   Gdx.input.isKeyPressed(Keys.CONTROL_LEFT);
        }
    };
    private static final ConsoleToggler DISABLED_CONSOLE_TOGGLER = new ConsoleToggler() {
        @Override
        public boolean shouldToggle(int keycode) {
            return false;
        }
    };

    private final ConsoleWidgetFactory widgetFactory;
    private final ShellFileSystem fileSystem;

    private ConsoleToggler consoleToggler = DEFAULT_CONSOLE_TOGGLER;
    private int maxTerminalEntries = 30;
    private int maxCommandHistory = 30;
    private String welcomeMessage = "Welcome to Jerminal!\n";

    public LibGdxConsoleBuilder(ShellFileSystem fileSystem, ConsoleWidgetFactory widgetFactory) {
        this.fileSystem = Objects.requireNonNull(fileSystem);
        this.widgetFactory = Objects.requireNonNull(widgetFactory);
    }

    public LibGdxConsole build() {
        return new LibGdxConsole(fileSystem, widgetFactory, consoleToggler, maxTerminalEntries, maxCommandHistory, welcomeMessage);
    }

    public LibGdxConsoleBuilder setMaxTerminalEntries(int maxTerminalEntries) {
        this.maxTerminalEntries = maxTerminalEntries;
        return this;
    }

    public LibGdxConsoleBuilder setMaxCommandHistory(int maxCommandHistory) {
        this.maxCommandHistory = maxCommandHistory;
        return this;
    }

    public LibGdxConsoleBuilder setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = Objects.requireNonNull(welcomeMessage);
        return this;
    }

    public LibGdxConsoleBuilder setConsoleToggler(ConsoleToggler consoleToggler) {
        this.consoleToggler = Objects.requireNonNull(consoleToggler);
        return this;
    }

    public LibGdxConsoleBuilder disableConsoleToggler() {
        this.consoleToggler = DISABLED_CONSOLE_TOGGLER;
        return this;
    }
}
