package com.rawcod.jerminal.libgdx;

import com.badlogic.gdx.Input.Keys;
import com.github.ykrasik.jerminal.api.Shell;
import com.github.ykrasik.jerminal.api.ShellBuilder;
import com.github.ykrasik.jerminal.api.command.ShellCommand;

/**
 * User: ykrasik
 * Date: 11/08/2014
 * Time: 23:43
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
}
