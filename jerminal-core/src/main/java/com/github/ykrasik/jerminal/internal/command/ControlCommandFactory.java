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

import com.github.ykrasik.jerminal.api.command.*;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.command.parameter.flag.FlagParamBuilder;
import com.github.ykrasik.jerminal.api.command.parameter.view.ShellCommandParamView;
import com.github.ykrasik.jerminal.api.command.view.ShellCommandView;
import com.github.ykrasik.jerminal.api.exception.ExecuteException;
import com.github.ykrasik.jerminal.api.filesystem.ShellEntryView;
import com.github.ykrasik.jerminal.api.output.OutputProcessor;
import com.github.ykrasik.jerminal.internal.command.parameter.entry.DirectoryParamBuilder;
import com.github.ykrasik.jerminal.internal.command.parameter.entry.FileParamBuilder;
import com.github.ykrasik.jerminal.internal.command.parameter.view.ShellCommandParamViewImpl;
import com.github.ykrasik.jerminal.internal.command.view.ShellCommandViewImpl;
import com.github.ykrasik.jerminal.internal.filesystem.ShellEntry;
import com.github.ykrasik.jerminal.internal.filesystem.ShellFileSystem;
import com.github.ykrasik.jerminal.internal.filesystem.directory.ShellDirectory;
import com.github.ykrasik.jerminal.internal.filesystem.file.ShellFile;
import com.github.ykrasik.jerminal.internal.filesystem.view.ShellEntryViewImpl;
import com.google.common.base.Supplier;

import java.util.*;

/**
 * Creates all the default Control {@link Command}s.
 *
 * @author Yevgeny Krasik
 */
public class ControlCommandFactory {
    private static final String CHANGE_DIRECTORY_COMMAND_NAME = "cd";
    private static final String LIST_DIRECTORY_COMMAND_NAME = "ls";
    private static final String DESCRIBE_COMMAND_COMMAND_NAME = "man";
    private static final String PRINT_WORKING_DIRECTORY_COMMAND_NAME = "pwd";

    private static final ShellEntryViewComparator COMPARATOR = new ShellEntryViewComparator();

    private final ShellFileSystem fileSystem;
    private final OutputProcessor outputProcessor;

    public ControlCommandFactory(ShellFileSystem fileSystem, OutputProcessor outputProcessor) {
        this.fileSystem = fileSystem;
        this.outputProcessor = outputProcessor;
    }

    // TODO: Add the following commands: list all commands

    public Set<Command> createControlCommands() {
        final Set<Command> controlCommands = new HashSet<>();
        controlCommands.add(createChangeDirectoryCommand());
        controlCommands.add(createListDirectoryCommand());
        controlCommands.add(createDescribeCommandCommand());
        controlCommands.add(createPrintWorkingDirectoryCommand());
        return controlCommands;
    }

    public Command createChangeDirectoryCommand() {
        return new ShellCommandBuilder(CHANGE_DIRECTORY_COMMAND_NAME)
            .setDescription("Change current directory")
            .addParam(
                new DirectoryParamBuilder("dir", fileSystem)
                    .setDescription("Directory to change to")
                    .build()
            )
            .setExecutor(new CommandExecutor() {
                @Override
                public void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException {
                    final ShellDirectory directory = args.popDirectory();
                    fileSystem.setCurrentDirectory(directory);
                }
            })
            .build();
    }

    public Command createListDirectoryCommand() {
        return new ShellCommandBuilder(LIST_DIRECTORY_COMMAND_NAME)
            .setDescription("List directory content")
            .addParam(
                new DirectoryParamBuilder("dir", fileSystem)
                    .setDescription("Directory to list")
                    .setOptional(new Supplier<ShellDirectory>() {
                        @Override
                        public ShellDirectory get() {
                            return fileSystem.getCurrentDirectory();
                        }
                    })
                    .build()
            )
            .addParam(
                new FlagParamBuilder("-r")
                    .setDescription("Whether to recurse into sub-directories")
                    .build()
            )
            .setExecutor(new CommandExecutor() {
                @Override
                public void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException {
                    final ShellDirectory directory = args.popDirectory();
                    final boolean recursive = args.popBool();
                    final ShellEntryView shellEntryView = createShellEntryView(directory, recursive);
                    outputProcessor.displayShellEntryView(shellEntryView);
                }
            })
            .build();
    }

    private ShellEntryView createShellEntryView(ShellDirectory directory, boolean recursive) {
        final Collection<ShellEntry> children = directory.getChildren();
        final List<ShellEntryView> viewChildren = new ArrayList<>(children.size());
        for (ShellEntry child : children) {
            final ShellEntryView childToAdd;
            if (child.isDirectory() && recursive) {
                childToAdd = createShellEntryView((ShellDirectory) child, true);
            } else {
                childToAdd = new ShellEntryViewImpl(child.getName(), child.getDescription(), child.isDirectory(), Collections.<ShellEntryView>emptyList());
            }
            viewChildren.add(childToAdd);
        }
        Collections.sort(viewChildren, COMPARATOR);
        return new ShellEntryViewImpl(directory.getName(), directory.getDescription(), true, viewChildren);
    }

    public Command createDescribeCommandCommand() {
        return new ShellCommandBuilder(DESCRIBE_COMMAND_COMMAND_NAME)
            .setDescription("Describe command")
            .addParam(
                new FileParamBuilder("cmd", fileSystem)
                    .setDescription("Command to describe")
                    .build()
            )
            .setExecutor(new CommandExecutor() {
                @Override
                public void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException {
                    final ShellFile file = args.popFile();
                    final Command command = file.getCommand();
                    final ShellCommandView shellCommandView = createShellCommandView(command);
                    outputProcessor.displayShellCommandView(shellCommandView);
                }
            })
            .build();
    }

    private ShellCommandView createShellCommandView(Command command) {
        final List<ShellCommandParamView> params = describeCommandParams(command.getParams());
        return new ShellCommandViewImpl(command.getName(), command.getDescription(), params);
    }

    private List<ShellCommandParamView> describeCommandParams(List<CommandParam> params) {
        final List<ShellCommandParamView> paramViews = new ArrayList<>(params.size());
        for (CommandParam param : params) {
            final ShellCommandParamView paramView = new ShellCommandParamViewImpl(param.getName(), param.getDescription(), param.getType(), param.getExternalForm());
            paramViews.add(paramView);
        }
        return paramViews;
    }

    public Command createPrintWorkingDirectoryCommand() {
        return new ShellCommandBuilder(PRINT_WORKING_DIRECTORY_COMMAND_NAME)
            .setDescription("Print working directory")
            .setExecutor(new CommandExecutor() {
                @Override
                public void execute(CommandArgs args, OutputPrinter outputPrinter) throws ExecuteException {
                    final String workingDirectoryStr = createWorkingDirectoryString();
                    outputPrinter.println(workingDirectoryStr);
                }
            })
            .build();
    }

    private String createWorkingDirectoryString() {
        final ShellDirectory currentDirectory = fileSystem.getCurrentDirectory();
        final StringBuilder sb = new StringBuilder();
        doCreateWorkingDirectoryString(currentDirectory, sb);
        return sb.toString();
    }

    private void doCreateWorkingDirectoryString(ShellDirectory currentDirectory, StringBuilder sb) {
        if (currentDirectory == fileSystem.getRoot()) {
            sb.append('/');
            return;
        }

        final ShellDirectory parent = currentDirectory.getParent().get();
        doCreateWorkingDirectoryString(parent, sb);

        sb.append(currentDirectory.getName());
        sb.append('/');
    }

    private static class ShellEntryViewComparator implements Comparator<ShellEntryView> {
        @Override
        public int compare(ShellEntryView o1, ShellEntryView o2) {
            if (o1.isDirectory() && !o2.isDirectory()) {
                return 1;
            }
            if (o2.isDirectory() && !o1.isDirectory()) {
                return -1;
            }
            return o1.getName().compareTo(o2.getName());
        }
    }
}
