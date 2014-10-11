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
import com.github.ykrasik.jerminal.api.Shell;
import com.github.ykrasik.jerminal.api.ShellImpl;
import com.github.ykrasik.jerminal.api.display.DisplayDriver;
import com.github.ykrasik.jerminal.api.display.terminal.TerminalDisplayDriver;
import com.github.ykrasik.jerminal.api.filesystem.ShellFileSystem;

import java.util.Objects;

/**
 * A builder for a {@link LibGdxConsole}.<br>
 *
 * @author Yevgeny Krasik
 */
// FIXME: Explain the toggle functionality.
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

    private final LibGdxConsoleWidgetFactory widgetFactory;

    private final LibGdxTerminal terminal;
    private ShellFileSystem fileSystem;
    private int maxHistory = 30;
    private String welcomeMessage = "Welcome to Jerminal!\n";
    private ConsoleToggler consoleToggler = DEFAULT_CONSOLE_TOGGLER;

    public LibGdxConsoleBuilder(LibGdxConsoleWidgetFactory widgetFactory, int maxBufferEntries) {
        this.widgetFactory = widgetFactory;
        this.terminal = new LibGdxTerminal(widgetFactory, maxBufferEntries);
    }

    public LibGdxConsole build() {
        final DisplayDriver displayDriver = new TerminalDisplayDriver(terminal);
        final Shell shell = new ShellImpl(fileSystem, displayDriver, maxHistory, welcomeMessage);
        return new LibGdxConsole(terminal, shell, consoleToggler, widgetFactory);
    }

    public LibGdxConsoleBuilder setFileSystem(ShellFileSystem fileSystem) {
        this.fileSystem = Objects.requireNonNull(fileSystem);
        return this;
    }

    public LibGdxConsoleBuilder setMaxCommandHistory(int maxHistory) {
        this.maxHistory = maxHistory;
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
