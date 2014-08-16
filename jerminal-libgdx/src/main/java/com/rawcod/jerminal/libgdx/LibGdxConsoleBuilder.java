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

package com.rawcod.jerminal.libgdx;

import com.badlogic.gdx.Input.Keys;
import com.github.ykrasik.jerminal.api.Shell;
import com.github.ykrasik.jerminal.api.ShellBuilder;
import com.github.ykrasik.jerminal.api.command.ShellCommand;

import java.util.Collection;

/**
 * A builder for a {@link LibGdxConsole}.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxConsoleBuilder {
    private final LibGdxTerminal terminal;
    private final ShellBuilder builder;

    private int toggleKeycode = Keys.GRAVE;

    public LibGdxConsoleBuilder(float width,
                                float height,
                                int maxBufferEntries,
                                LibGdxConsoleWidgetFactory widgetFactory) {
        this.terminal = new LibGdxTerminal(width, height, maxBufferEntries, widgetFactory);
        this.builder = new ShellBuilder(terminal);
    }

    public LibGdxConsole build() {
        final Shell shell = builder.build();
        return new LibGdxConsole(terminal, shell, toggleKeycode);
    }

    public LibGdxConsoleBuilder setMaxCommandHistory(int maxCommandHistory) {
        builder.setMaxCommandLineHistory(maxCommandHistory);
        return this;
    }

    public LibGdxConsoleBuilder setToggleKeycode(int toggleKeycode) {
        this.toggleKeycode = toggleKeycode;
        return this;
    }

    public LibGdxConsoleBuilder add(ShellCommand... commands) {
        builder.add(commands);
        return this;
    }

    public LibGdxConsoleBuilder add(String path, ShellCommand... commands) {
        builder.add(path, commands);
        return this;
    }

    public LibGdxConsoleBuilder addGlobalCommands(ShellCommand... globalCommands) {
        builder.addGlobalCommands(globalCommands);
        return this;
    }

    public LibGdxConsoleBuilder addGlobalCommands(Collection<ShellCommand> globalCommands) {
        builder.addGlobalCommands(globalCommands);
        return this;
    }
}
