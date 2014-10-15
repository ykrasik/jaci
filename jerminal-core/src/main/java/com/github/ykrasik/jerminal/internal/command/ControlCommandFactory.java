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

package com.github.ykrasik.jerminal.internal.command;

import com.github.ykrasik.jerminal.ShellConstants;
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
    private static final String PRINT_WORKING_DIRECTORY_COMMAND_NAME = "pwd";

    private final InternalShellFileSystem fileSystem;
    private final DisplayDriver displayDriver;

    public ControlCommandFactory(InternalShellFileSystem fileSystem, DisplayDriver displayDriver) {
        this.fileSystem = fileSystem;
        this.displayDriver = displayDriver;
    }

    // TODO: Add the following commands: list all commands

    /**
     * Install the default control commands.
     */
    public void installControlCommands() {
        // TODO: DOn't install directory navigation commands if no directories in fileSystem?
        fileSystem.addGlobalCommands(
            createChangeDirectoryCommand(),
            createListDirectoryCommand(),
            createDescribeCommandCommand(),
            createPrintWorkingDirectoryCommand()
        );
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
                }
            })
            .build();
    }

    /**
     * @return Create the print working directory command.
     */
    public Command createPrintWorkingDirectoryCommand() {
        return new CommandBuilder(PRINT_WORKING_DIRECTORY_COMMAND_NAME)
            .setDescription("Print path to working directory")
            .setExecutor(new CommandExecutor() {
                @Override
                public void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException {
                    final InternalShellDirectory workingDirectory = fileSystem.getWorkingDirectory();
                    final List<InternalShellDirectory> path = fileSystem.getPath(workingDirectory);
                    final String workingDirectoryStr = serializePath(path);
                    outputPrinter.println(workingDirectoryStr);
                }
            })
            .build();
    }

    private String serializePath(List<InternalShellDirectory> path) {
        final StringBuilder sb = new StringBuilder();
        // All paths should start with '/'.
        sb.append(ShellConstants.FILE_SYSTEM_DELIMITER);

        // The first element is always the root, skip it.
        for (int i = 1; i < path.size(); i++) {
            final InternalShellDirectory directory = path.get(i);
            sb.append(directory.getName());
            sb.append(ShellConstants.FILE_SYSTEM_DELIMITER);
        }
        return sb.toString();
    }
}
