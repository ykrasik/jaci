/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jemi.cli.hierarchy;

import com.github.ykrasik.jemi.Identifier;
import com.github.ykrasik.jemi.api.CommandOutput;
import com.github.ykrasik.jemi.cli.command.CliCommand;
import com.github.ykrasik.jemi.cli.command.CliCommandOutput;
import com.github.ykrasik.jemi.cli.directory.CliDirectory;
import com.github.ykrasik.jemi.cli.param.BooleanCliParam;
import com.github.ykrasik.jemi.cli.param.CliParam;
import com.github.ykrasik.jemi.cli.param.CommandCliParam;
import com.github.ykrasik.jemi.cli.param.DirectoryCliParam;
import com.github.ykrasik.jemi.command.CommandArgs;
import com.github.ykrasik.jemi.command.CommandExecutor;
import com.github.ykrasik.jemi.util.function.Spplr;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Creates the system commands of a CLI.
 * Most system commands require an already built {@link CliCommandHierarchy}, but system commands are also a part
 * of the CliCommandHierarchy, creating a circular dependency. This is resolved in the CliCommandHierarchy itself
 * by offering a 'promise' object which will eventually delegate to the real implementation.
 *
 * @author Yevgeny Krasik
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CliSystemCommandFactory {
    private final CliCommandHierarchy hierarchy;

    // TODO: Add the following commands: list all commands

    /**
     * @return Create the change directory command.
     */
    CliCommand createChangeDirectoryCommand() {
        final Identifier identifier = new Identifier("cd", "Change working directory");
        final List<CliParam> params = Collections.<CliParam>singletonList(
            new DirectoryCliParam.Builder("dir", hierarchy).setDescription("Directory to change to").build()
        );
        return CliCommand.from(identifier, params, new CommandExecutor() {
            @Override
            public void execute(CommandOutput output, CommandArgs args) throws Exception {
                final CliDirectory directory = args.popArg();
                hierarchy.setWorkingDirectory(directory);
                ((CliCommandOutput) output).setWorkingDirectory(directory);
            }
        });
    }

    /**
     * @return Create the list directory command.
     */
    CliCommand createListDirectoryCommand() {
        final Identifier identifier = new Identifier("ls", "List directory content");
        final List<CliParam> params = Arrays.<CliParam>asList(
            new DirectoryCliParam.Builder("dir", hierarchy)
                .setDescription("Directory to list")
                .setOptional(new Spplr<CliDirectory>() {
                    @Override
                    public CliDirectory get() {
                        return hierarchy.getWorkingDirectory();
                    }
                })
                .build(),
            BooleanCliParam.optional(new Identifier("r", "Whether to recurse into sub-directories"), false)
        );
        return CliCommand.from(identifier, params, new CommandExecutor() {
            @Override
            public void execute(CommandOutput output, CommandArgs args) throws Exception {
                final CliDirectory directory = args.popArg();
                final boolean recursive = args.popArg();
                ((CliCommandOutput) output).printDirectory(directory, recursive);
            }
        });
    }

    /**
     * @return Create the describe command command.
     */
    CliCommand createDescribeCommandCommand() {
        final Identifier identifier = new Identifier("man", "Describe command");
        final List<CliParam> params = Collections.<CliParam>singletonList(
            new CommandCliParam.Builder("cmd", hierarchy).setDescription("Command to describe").build()
        );
        return CliCommand.from(identifier, params, new CommandExecutor() {
            @Override
            public void execute(CommandOutput output, CommandArgs args) throws Exception {
                final CliCommand command = args.popArg();
                ((CliCommandOutput) output).printCommand(command);
            }
        });
    }

    /**
     * Create a directory containing all system commands. It is convenient to store all system commands in a directory.
     * Most system commands require an already built {@link CliCommandHierarchy}, but system commands are also a part
     * of the CliCommandHierarchy, creating a circular dependency. This is resolved in the CliCommandHierarchy itself
     * by offering a 'promise' object which will eventually delegate to the real implementation.
     *
     * @param hierarchy Hierarchy on which the system commands will operate.
     * @return A {@link CliDirectory} containing all system commands.
     */
    public static CliDirectory from(@NonNull CliCommandHierarchy hierarchy) {
        final Identifier identifier = new Identifier("system", "System commands");
        final CliSystemCommandFactory factory = new CliSystemCommandFactory(hierarchy);
        return CliDirectory.from(
            identifier,
            factory.createChangeDirectoryCommand(),
            factory.createListDirectoryCommand(),
            factory.createDescribeCommandCommand()
        );
    }
}
