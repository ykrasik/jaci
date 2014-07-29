package com.rawcod.jerminal.command;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.Params;
import com.rawcod.jerminal.command.parameters.entry.DirectoryParam;
import com.rawcod.jerminal.command.parameters.entry.DirectoryParamBuilder;
import com.rawcod.jerminal.command.parameters.flag.FlagParamBuilder;
import com.rawcod.jerminal.filesystem.FileSystemManager;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommandBuilder;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValue;

import java.util.List;
import java.util.Set;

/**
 * User: ykrasik
 * Date: 27/07/2014
 * Time: 23:55
 */
public class GlobalCommandFactory {
    private static final String CHANGE_DIRECTORY_COMMAND_NAME = "cd";
    private static final String LIST_DIRECTORY_COMMAND_NAME = "ls";

    public static ShellCommand createChangeDirectoryCommand(final FileSystemManager fileSystemManager) {
        final String dirArgName = "dir";
        return new ShellCommandBuilder(CHANGE_DIRECTORY_COMMAND_NAME)
            .setDescription("Change current directory")
            .addParam(
                new DirectoryParamBuilder(dirArgName)
                    .setDescription("Directory to change to")
                    .build()
            )
            .setExecutor(new CommandExecutor() {
                @Override
                public ExecuteReturnValue execute(CommandArgs args, ExecutionContext context) {
                    final ShellDirectory directory = args.getDirectory(dirArgName);
                    fileSystemManager.setCurrentDirectory(directory);
                }
            })
            .build();
    }

    private static ShellCommand createListDirectoryCommand(final FileSystemManager fileSystemManager) {
        final String dirArgName = "dir";
        final String recursiveArgName = "-r";
        return new ShellCommandBuilder(LIST_DIRECTORY_COMMAND_NAME)
            .setDescription("List directory content")
            .addParam(
                new DirectoryParamBuilder(dirArgName)
                    .setDescription("Directory to list")
                    .setOptional(new Supplier<ShellDirectory>() {
                        @Override
                        public ShellDirectory get() {
                            return fileSystemManager.getCurrentDirectory();
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
                public ExecuteReturnValue execute(CommandArgs args, ExecutionContext context) {
                    final ShellDirectory directory = args.getDirectory(dirArgName);
                    final boolean recursive = args.getBool(recursiveArgName);
                    // FIXME: How to return this to the outputProcessors?
                }
            })
            .build();
    }
}
