package com.rawcod.jerminal.command.factory;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.command.OutputBuffer;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.entry.DirectoryParamBuilder;
import com.rawcod.jerminal.command.parameters.entry.FileParamBuilder;
import com.rawcod.jerminal.command.parameters.flag.FlagParamBuilder;
import com.rawcod.jerminal.command.view.ShellCommandParamView;
import com.rawcod.jerminal.command.view.ShellCommandParamViewImpl;
import com.rawcod.jerminal.command.view.ShellCommandView;
import com.rawcod.jerminal.command.view.ShellCommandViewImpl;
import com.rawcod.jerminal.exception.ExecuteException;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommandBuilder;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.filesystem.entry.view.ShellEntryView;
import com.rawcod.jerminal.filesystem.entry.view.ShellEntryViewImpl;
import com.rawcod.jerminal.output.OutputProcessor;

import java.util.*;

/**
 * User: ykrasik
 * Date: 27/07/2014
 * Time: 23:55
 */
public class ControlCommandFactory {
    private static final String CHANGE_DIRECTORY_COMMAND_NAME = "cd";
    private static final String LIST_DIRECTORY_COMMAND_NAME = "ls";
    private static final String DESCRIBE_COMMAND_COMMAND_NAME = "man";

    private static final ShellEntryViewComparator COMPARATOR = new ShellEntryViewComparator();

    private final ShellFileSystem fileSystem;
    private final OutputProcessor outputProcessor;

    public ControlCommandFactory(ShellFileSystem fileSystem, OutputProcessor outputProcessor) {
        this.fileSystem = fileSystem;
        this.outputProcessor = outputProcessor;
    }

    public Set<ShellCommand> createControlCommands() {
        final Set<ShellCommand> globalCommands = new HashSet<>();
        globalCommands.add(createChangeDirectoryCommand());
        globalCommands.add(createListDirectoryCommand());
        globalCommands.add(createDescribeCommandCommand());
        return globalCommands;
    }

    public ShellCommand createChangeDirectoryCommand() {
        final String dirArgName = "dir";
        return new ShellCommandBuilder(CHANGE_DIRECTORY_COMMAND_NAME)
            .setDescription("Change current directory")
            .addParam(
                new DirectoryParamBuilder(dirArgName, fileSystem)
                    .setDescription("Directory to change to")
                    .build()
            )
            .setExecutor(new CommandExecutor() {
                @Override
                public void execute(CommandArgs args, OutputBuffer output) throws ExecuteException {
                    final ShellDirectory directory = args.getDirectory(dirArgName);
                    fileSystem.setCurrentDirectory(directory);
                }
            })
            .build();
    }

    public ShellCommand createListDirectoryCommand() {
        final String dirArgName = "dir";
        final String recursiveArgName = "-r";
        return new ShellCommandBuilder(LIST_DIRECTORY_COMMAND_NAME)
            .setDescription("List directory content")
            .addParam(
                new DirectoryParamBuilder(dirArgName, fileSystem)
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
                new FlagParamBuilder(recursiveArgName)
                    .setDescription("Whether to recurse into sub-directories")
                    .build()
            )
            .setExecutor(new CommandExecutor() {
                @Override
                public void execute(CommandArgs args, OutputBuffer output) throws ExecuteException {
                    final ShellDirectory directory = args.getDirectory(dirArgName);
                    final boolean recursive = args.getBool(recursiveArgName);
                    final ShellEntryView shellEntryView = listDirectory(directory, recursive);
                    outputProcessor.displayShellEntryView(shellEntryView);
                }
            })
            .build();
    }

    private ShellEntryView listDirectory(ShellDirectory directory, boolean recursive) {
        final Collection<ShellEntry> children = directory.getChildren();
        final List<ShellEntryView> viewChildren = new ArrayList<>(children.size());
        for (ShellEntry child : children) {
            final ShellEntryView childToAdd;
            if (child.isDirectory() && recursive) {
                childToAdd = listDirectory((ShellDirectory) child, true);
            } else {
                childToAdd = new ShellEntryViewImpl(child.getName(), child.getDescription(), child.isDirectory(), Collections.<ShellEntryView>emptyList());
            }
            viewChildren.add(childToAdd);
        }
        Collections.sort(viewChildren, COMPARATOR);
        return new ShellEntryViewImpl(directory.getName(), directory.getDescription(), true, viewChildren);
    }

    public ShellCommand createDescribeCommandCommand() {
        final String commandArgName = "cmd";
        return new ShellCommandBuilder(DESCRIBE_COMMAND_COMMAND_NAME)
            .setDescription("Describe command")
            .addParam(
                new FileParamBuilder(commandArgName, fileSystem)
                    .setDescription("Command to describe")
                    .build()
            )
            .setExecutor(new CommandExecutor() {
                @Override
                public void execute(CommandArgs args, OutputBuffer output) throws ExecuteException {
                    final ShellCommand command = args.getFile(commandArgName);
                    final ShellCommandView shellCommandView = describeCommand(command);
                    outputProcessor.displayShellCommandView(shellCommandView);
                }
            })
            .build();
    }

    private ShellCommandView describeCommand(ShellCommand command) {
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
