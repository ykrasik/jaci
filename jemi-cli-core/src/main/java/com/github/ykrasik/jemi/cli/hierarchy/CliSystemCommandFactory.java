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

import com.github.ykrasik.jemi.cli.command.CliCommand;
import com.github.ykrasik.jemi.cli.command.CliCommandArgs;
import com.github.ykrasik.jemi.cli.command.CliCommandExecutor;
import com.github.ykrasik.jemi.cli.command.CliCommandOutput;
import com.github.ykrasik.jemi.cli.directory.CliDirectory;
import com.github.ykrasik.jemi.cli.param.BooleanCliParam;
import com.github.ykrasik.jemi.cli.param.CliParam;
import com.github.ykrasik.jemi.cli.param.CommandCliParam;
import com.github.ykrasik.jemi.cli.param.DirectoryCliParam;
import com.github.ykrasik.jemi.core.Identifier;
import com.github.ykrasik.jemi.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)  // Package-visible for testing
public class CliSystemCommandFactory {
    @NonNull private final CliCommandHierarchy hierarchy;

    // TODO: Add the following commands: list all commands

    /**
     * @return Create the change directory command.
     */
    CliCommand createChangeDirectoryCommand() {
        final Identifier identifier = new Identifier("cd", "Change working directory");
        final List<CliParam> params = Collections.<CliParam>singletonList(
            new DirectoryCliParam.Builder("dir", hierarchy).setDescription("Directory to change to").build()
        );
        return new CliCommand(identifier, params, new CliCommandExecutor() {
            @Override
            public void execute(CliCommandOutput output, CliCommandArgs args) throws Exception {
                final CliDirectory directory = args.popDirectory();
                hierarchy.setWorkingDirectory(directory);
                output.setWorkingDirectory(directory);
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
                .setOptional(new Supplier<CliDirectory>() {
                    @Override
                    public CliDirectory get() {
                        return hierarchy.getWorkingDirectory();
                    }
                })
                .build(),
            BooleanCliParam.optional(new Identifier("r", "Whether to recurse into sub-directories"), false)
        );
        return new CliCommand(identifier, params, new CliCommandExecutor() {
            @Override
            public void execute(CliCommandOutput output, CliCommandArgs args) throws Exception {
                final CliDirectory directory = args.popDirectory();
                final boolean recursive = args.popBool();
                output.printDirectory(directory, recursive);
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
        return new CliCommand(identifier, params, new CliCommandExecutor() {
            @Override
            public void execute(CliCommandOutput output, CliCommandArgs args) throws Exception {
                final CliCommand command = args.popCommand();
                output.printCommand(command);
            }
        });
    }

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
