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
import com.github.ykrasik.jerminal.api.ShellBuilder;
import com.github.ykrasik.jerminal.api.command.Command;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A builder for a {@link LibGdxConsole}.<br>
 *
 * @author Yevgeny Krasik
 */
// FIXME: Explain the toggle functionality.
public class LibGdxConsoleBuilder {
    private static final ConsoleToggler DEFAULT_CONSOLE_TOGGLER = new DefaultConsoleToggler();
    private static final ConsoleToggler DISABLED_CONSOLE_TOGGLER = new DisabledConsoleToggler();

    private final LibGdxTerminal terminal;
    private final ShellBuilder builder;
    private final LibGdxConsoleWidgetFactory widgetFactory;

    private ConsoleToggler consoleToggler = DEFAULT_CONSOLE_TOGGLER;

    public LibGdxConsoleBuilder(LibGdxConsoleWidgetFactory widgetFactory, int maxBufferEntries) {
        this.widgetFactory = widgetFactory;
        this.terminal = new LibGdxTerminal(widgetFactory, maxBufferEntries);
        this.builder = new ShellBuilder(terminal);
    }

    public LibGdxConsole build() {
        final Shell shell = builder.build();
        return new LibGdxConsole(terminal, shell, consoleToggler, widgetFactory);
    }

    public LibGdxConsoleBuilder setMaxCommandHistory(int maxCommandHistory) {
        builder.setMaxCommandLineHistory(maxCommandHistory);
        return this;
    }

    public LibGdxConsoleBuilder setConsoleToggler(ConsoleToggler consoleToggler) {
        this.consoleToggler = checkNotNull(consoleToggler, "consoleToggler");
        return this;
    }

    public LibGdxConsoleBuilder disableConsoleToggler() {
        this.consoleToggler = DISABLED_CONSOLE_TOGGLER;
        return this;
    }

    public LibGdxConsoleBuilder add(Command... commands) {
        builder.add(commands);
        return this;
    }

    public LibGdxConsoleBuilder add(String path, Command... commands) {
        builder.add(path, commands);
        return this;
    }

    public LibGdxConsoleBuilder addGlobalCommands(Command... globalCommands) {
        builder.addGlobalCommands(globalCommands);
        return this;
    }

    public LibGdxConsoleBuilder addGlobalCommands(Collection<Command> globalCommands) {
        builder.addGlobalCommands(globalCommands);
        return this;
    }

    private static final class DefaultConsoleToggler implements ConsoleToggler {
        @Override
        public boolean shouldToggle(int keycode) {
            return keycode == Keys.GRAVE &&
                   Gdx.input.isKeyPressed(Keys.CONTROL_LEFT);
        }
    }

    private static final class DisabledConsoleToggler implements ConsoleToggler {
        @Override
        public boolean shouldToggle(int keycode) {
            return false;
        }
    }
}
