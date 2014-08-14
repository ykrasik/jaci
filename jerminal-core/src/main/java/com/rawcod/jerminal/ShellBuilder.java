package com.rawcod.jerminal;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.factory.ControlCommandFactory;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.ShellFileSystemBuilder;
import com.rawcod.jerminal.filesystem.ShellFileSystemPromise;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.output.OutputProcessor;
import com.rawcod.jerminal.output.terminal.Terminal;
import com.rawcod.jerminal.output.terminal.TerminalOutputProcessor;

import java.util.Collection;
import java.util.Set;

/**
 * User: ykrasik
 * Date: 11/08/2014
 * Time: 21:20
 */
public class ShellBuilder {
    private static final String VERSION = "0.1";
    private static final String DEFAULT_WELCOME_MESSAGE = "Welcome to Jerminal v" + VERSION + "!\n\n";

    private final OutputProcessor outputProcessor;
    private final ShellFileSystemBuilder fileSystemBuilder;
    private final ShellFileSystemPromise fileSystemPromise;

    private String welcomeMessage = DEFAULT_WELCOME_MESSAGE;
    private int maxCommandHistory = 20;

    public ShellBuilder(Terminal terminal) {
        this(new TerminalOutputProcessor(terminal));
    }

    public ShellBuilder(OutputProcessor outputProcessor) {
        this.outputProcessor = outputProcessor;
        this.fileSystemBuilder = new ShellFileSystemBuilder();
        this.fileSystemPromise = new ShellFileSystemPromise();

        final Set<ShellCommand> controlCommands = new ControlCommandFactory(fileSystemPromise, outputProcessor).createControlCommands();
        fileSystemBuilder.addGlobalCommands(controlCommands);
    }

    public Shell build() {
        final ShellFileSystem fileSystem = fileSystemBuilder.build();
        fileSystemPromise.setFileSystem(fileSystem);
        final ShellCommandHistory commandHistory = new ShellCommandHistory(maxCommandHistory);
        return new Shell(outputProcessor, fileSystem, commandHistory, welcomeMessage);
    }

    public ShellBuilder setMaxCommandHistory(int maxCommandHistory) {
        this.maxCommandHistory = maxCommandHistory;
        return this;
    }

    public ShellBuilder setWelcomeMessage(String welcomeMessage) {
       this.welcomeMessage = welcomeMessage;
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

    private static class DefaultWelcomeMessageSupplier implements Supplier<String> {
        private static final String VERSION = "0.1";

        @Override
        public String get() {
            return "Welcome to Jerminal v" + VERSION + ".\n\n\n";
        }
    }
}
