package com.rawcod.jerminal;

import com.rawcod.jerminal.command.factory.DefaultGlobalCommandFactory;
import com.rawcod.jerminal.filesystem.CurrentDirectoryContainer;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.ShellFileSystemBuilder;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.output.OutputHandler;
import com.rawcod.jerminal.output.OutputProcessor;

import java.util.Collection;
import java.util.Set;

/**
 * User: ykrasik
 * Date: 11/08/2014
 * Time: 21:20
 */
public class ShellBuilder {
    private final OutputProcessor outputProcessor;
    private final ShellFileSystemBuilder fileSystemBuilder;
    private final CurrentDirectoryContainer currentDirectoryContainer;

    private int maxCommandHistory = 20;

    public ShellBuilder(OutputHandler outputHandler) {
        this.outputProcessor = new OutputProcessor(outputHandler);
        this.currentDirectoryContainer = new CurrentDirectoryContainer();
        this.fileSystemBuilder = new ShellFileSystemBuilder(currentDirectoryContainer);

        final Set<ShellCommand> defaultGlobalCommands = new DefaultGlobalCommandFactory(currentDirectoryContainer, outputProcessor).createDefaultGlobalCommands();
        fileSystemBuilder.addGlobalCommands(defaultGlobalCommands);
    }

    public Shell build() {
        final ShellFileSystem fileSystem = fileSystemBuilder.build();
        final ShellCommandHistory commandHistory = new ShellCommandHistory(maxCommandHistory);
        return new Shell(outputProcessor, fileSystem, commandHistory);
    }

    public ShellBuilder setMaxCommandHistory(int maxCommandHistory) {
        this.maxCommandHistory = maxCommandHistory;
        return this;
    }

    public ShellBuilder add(ShellCommand... commands) {
        fileSystemBuilder.add(commands);
        return this;
    }

    public ShellBuilder add(String path, ShellCommand... commands) {
        fileSystemBuilder.add(path, commands);
        return this;
    }

    public ShellBuilder addGlobalCommands(ShellCommand... globalCommands) {
        fileSystemBuilder.addGlobalCommands(globalCommands);
        return this;
    }

    public ShellBuilder addGlobalCommands(Collection<ShellCommand> globalCommands) {
        fileSystemBuilder.addGlobalCommands(globalCommands);
        return this;
    }
}
