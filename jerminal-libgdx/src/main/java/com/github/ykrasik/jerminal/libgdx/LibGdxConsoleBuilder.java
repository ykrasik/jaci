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

/**
 * A builder for a {@link LibGdxJerminalConsole}.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxConsoleBuilder {
    private static final int DEFAULT_KEY_CODE = Keys.GRAVE;
    private static final int DISABLED_KEY_CODE = -2;

    private final LibGdxTerminal terminal;
    private final ShellBuilder builder;
    private final LibGdxConsoleWidgetFactory widgetFactory;

    private float width = Gdx.graphics.getWidth();
    private float height = Gdx.graphics.getHeight();
    private int toggleKeycode = DEFAULT_KEY_CODE;

    public LibGdxConsoleBuilder(LibGdxConsoleWidgetFactory widgetFactory, int maxBufferEntries) {
        this.widgetFactory = widgetFactory;
        this.terminal = new LibGdxTerminal(widgetFactory, maxBufferEntries);
        this.builder = new ShellBuilder(terminal);
    }

    public LibGdxJerminalConsole build() {
        final Shell shell = builder.build();
        return new LibGdxJerminalConsole(terminal, shell, widgetFactory, toggleKeycode, width, height);
    }

    public LibGdxConsoleBuilder setWidth(float width) {
        this.width = width;
        return this;
    }

    public LibGdxConsoleBuilder setHeight(float height) {
        this.height = height;
        return this;
    }

    public LibGdxConsoleBuilder setMaxCommandHistory(int maxCommandHistory) {
        builder.setMaxCommandLineHistory(maxCommandHistory);
        return this;
    }

    public LibGdxConsoleBuilder setToggleKeycode(int toggleKeycode) {
        this.toggleKeycode = toggleKeycode;
        return this;
    }

    public LibGdxConsoleBuilder disableToggleKeycode() {
        this.toggleKeycode = DISABLED_KEY_CODE;
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
}
