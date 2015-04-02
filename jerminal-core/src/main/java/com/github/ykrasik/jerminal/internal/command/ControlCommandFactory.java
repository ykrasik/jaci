/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jerminal.internal.command;

import com.github.ykrasik.jerminal.api.command.CommandArgs;
import com.github.ykrasik.jerminal.api.command.CommandBuilder;
import com.github.ykrasik.jerminal.api.command.CommandExecutor;
import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.command.parameter.flag.FlagParamBuilder;
import com.github.ykrasik.jerminal.api.display.DisplayDriver;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.github.ykrasik.jerminal.api.filesystem.directory.ShellDirectory;
import com.github.ykrasik.jerminal.internal.command.parameter.entry.DirectoryParamBuilder;
import com.github.ykrasik.jerminal.internal.command.parameter.entry.FileParamBuilder;
import com.github.ykrasik.jerminal.internal.filesystem.InternalShellFileSystem;
import com.github.ykrasik.jerminal.internal.filesystem.command.InternalCommand;
import com.github.ykrasik.jerminal.internal.filesystem.directory.InternalShellDirectory;
import com.google.common.base.Supplier;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Creates control {@link com.github.ykrasik.jerminal.api.filesystem.command.Command}s.
 *
 * @author Yevgeny Krasik
 */
public class ControlCommandFactory {
    private static final String CHANGE_DIRECTORY_COMMAND_NAME = "cd";
    private static final String LIST_DIRECTORY_COMMAND_NAME = "ls";
    private static final String DESCRIBE_COMMAND_COMMAND_NAME = "man";

    private final InternalShellFileSystem fileSystem;
    private final DisplayDriver displayDriver;

    public ControlCommandFactory(InternalShellFileSystem fileSystem, DisplayDriver displayDriver) {
        this.fileSystem = fileSystem;
        this.displayDriver = displayDriver;
    }

    // TODO: Add the following commands: list all commands

    /**
     * Install the default control commands.
     * If the file system does not contain any directories, directory navigation commands
     * will not be installed.
     */
    public void installControlCommands() {
        final List<Command> controlCommands = new LinkedList<>();
        controlCommands.addAll(Arrays.asList(
            createListDirectoryCommand(),
            createDescribeCommandCommand()
        ));

        // Don't install directory navigation commands if no directories in the fileSystem.
        if (fileSystem.containsDirectories()) {
            controlCommands.add(createChangeDirectoryCommand());
        }

        fileSystem.addGlobalCommands(controlCommands);
    }

    /**
     * @return Create the change directory command.
     */
    public Command createChangeDirectoryCommand() {
        return new CommandBuilder(CHANGE_DIRECTORY_COMMAND_NAME)
            .setDescription("Change working directory")
            .addParam(new DirectoryParamBuilder("dir", fileSystem).setDescription("Directory to change to").build())
            .setExecutor(new CommandExecutor() {
                @Override
                public void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException {
                    final InternalShellDirectory directory = ((PrivilegedCommandArgs) args).popDirectory();
                    fileSystem.setWorkingDirectory(directory);
                }
            })
            .build();
    }

    /**
     * @return Create the list directory command.
     */
    public Command createListDirectoryCommand() {
        return new CommandBuilder(LIST_DIRECTORY_COMMAND_NAME)
            .setDescription("List directory content")
            .addParam(
                new DirectoryParamBuilder("dir", fileSystem)
                    .setDescription("Directory to list")
                    .setOptional(new Supplier<InternalShellDirectory>() {
                        @Override
                        public InternalShellDirectory get() {
                            return fileSystem.getWorkingDirectory();
                        }
                    })
                    .build()
            )
            .addParam(new FlagParamBuilder("-r").setDescription("Whether to recurse into sub-directories").build())
            .setExecutor(new CommandExecutor() {
                @Override
                public void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException {
                    final InternalShellDirectory directory = ((PrivilegedCommandArgs) args).popDirectory();
                    final boolean recursive = args.popBool();
                    final ShellDirectory shellDirectory = directory.toShellDirectory(recursive);
                    displayDriver.displayDirectory(shellDirectory);
                    suppressDefaultExecutionMessage(outputPrinter);
                }
            })
            .build();
    }

    /**
     * @return Create the describe command command.
     */
    public Command createDescribeCommandCommand() {
        return new CommandBuilder(DESCRIBE_COMMAND_COMMAND_NAME)
            .setDescription("Describe command")
            .addParam(new FileParamBuilder("cmd", fileSystem).setDescription("Command to describe").build())
            .setExecutor(new CommandExecutor() {
                @Override
                public void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException {
                    final InternalCommand internalCommand = ((PrivilegedCommandArgs) args).popCommand();
                    final Command command = internalCommand.getCommand();
                    displayDriver.displayCommand(command);
                    suppressDefaultExecutionMessage(outputPrinter);
                }
            })
            .build();
    }

    private void suppressDefaultExecutionMessage(OutputPrinter outputPrinter) {
        ((PrivilegedOutputPrinter) outputPrinter).suppressDefaultExecutionMessage();
    }
}
