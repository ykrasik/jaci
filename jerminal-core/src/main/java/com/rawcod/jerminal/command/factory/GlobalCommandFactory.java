package com.rawcod.jerminal.command.factory;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.command.ExecutionContext;
import com.rawcod.jerminal.command.parameters.entry.DirectoryParamBuilder;
import com.rawcod.jerminal.command.parameters.flag.FlagParamBuilder;
import com.rawcod.jerminal.filesystem.FileSystemManager;
import com.rawcod.jerminal.filesystem.entry.ShellEntry;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommandBuilder;
import com.rawcod.jerminal.filesystem.entry.directory.ShellFolder;
import com.rawcod.jerminal.output.OutputHandler;
import com.rawcod.jerminal.returnvalue.execute.executor.ExecutorReturnValue;

import java.util.List;

/**
 * User: ykrasik
 * Date: 27/07/2014
 * Time: 23:55
 */
public class GlobalCommandFactory {
    private static final String CHANGE_DIRECTORY_COMMAND_NAME = "cd";
    private static final String LIST_DIRECTORY_COMMAND_NAME = "ls";

    private final FileSystemManager fileSystemManager;
    private final OutputHandler outputHandler;

    public GlobalCommandFactory(FileSystemManager fileSystemManager, OutputHandler outputHandler) {
        this.fileSystemManager = fileSystemManager;
        this.outputHandler = outputHandler;
    }

    public ShellCommand createChangeDirectoryCommand() {
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
                public ExecutorReturnValue execute(CommandArgs args, ExecutionContext context) {
                    final ShellFolder directory = args.getDirectory(dirArgName);
                    fileSystemManager.setCurrentFolder(directory);
                    return success();
                }
            })
            .build();
    }

    private ShellCommand createListDirectoryCommand() {
        final String dirArgName = "dir";
        final String recursiveArgName = "-r";
        return new ShellCommandBuilder(LIST_DIRECTORY_COMMAND_NAME)
            .setDescription("List directory content")
            .addParam(
                new DirectoryParamBuilder(dirArgName)
                    .setDescription("Directory to list")
                    .setOptional(new Supplier<ShellFolder>() {
                        @Override
                        public ShellFolder get() {
                            return fileSystemManager.getCurrentFolder();
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
                public ExecutorReturnValue execute(CommandArgs args, ExecutionContext context) {
                    final ShellFolder directory = args.getDirectory(dirArgName);
                    final boolean recursive = args.getBool(recursiveArgName);
                    // FIXME: How to return this to the outputProcessors?
                }
            })
            .build();
    }

    private ShellTree listContent(ShellFolder directory, boolean recursive) {
        return createShellTreeNode(this, true, !recursive);
    }

    private ShellTree createShellTreeNode(ShellEntry shellEntry, boolean recursive, boolean recurseOnce) {
        final ShellTree root = new ShellTree(shellEntry.getName(), shellEntry.getDescription(), shellEntry.getDescription(), shellEntry.isDirectory());
        if (recursive && shellEntry.isDirectory()) {
            final List<ShellEntry> children = ((ShellFolder) shellEntry).getChildren();
            for (ShellEntry child : children) {
                final ShellTree node = createShellTreeNode(child, !recurseOnce, recurseOnce);
                root.addChild(node);
            }
        }
        return root;
    }
}
