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

package com.github.ykrasik.jerminal.api;

import com.github.ykrasik.jerminal.internal.CommandLineHistory;
import com.github.ykrasik.jerminal.internal.ShellImpl;
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
 * A builder for a {@link Shell}.
 *
 * @author Yevgeny Krasik
 */
public class ShellBuilder {
    private static final String VERSION = "0.1";
    private static final String DEFAULT_WELCOME_MESSAGE = "Welcome to Jerminal v" + VERSION + "!\n\n";

    private final OutputProcessor outputProcessor;
    private final ShellFileSystemBuilder fileSystemBuilder;
    private final ShellFileSystemPromise fileSystemPromise;

    private String welcomeMessage = DEFAULT_WELCOME_MESSAGE;
    private int maxCommandLineHistory = 20;

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
        final CommandLineHistory commandHistory = new CommandLineHistory(maxCommandLineHistory);
        return new ShellImpl(outputProcessor, fileSystem, commandHistory, welcomeMessage);
    }

    public ShellBuilder setMaxCommandLineHistory(int maxCommandLineHistory) {
        this.maxCommandLineHistory = maxCommandLineHistory;
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
}