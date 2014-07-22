package com.rawcod.jerminal.manager;

import com.rawcod.jerminal.command.args.CommandArgs;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.parse.args.ParseCommandArgsReturnValue;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.returnvalue.parse.entry.ParsePathReturnValue;
import com.rawcod.jerminal.returnvalue.parse.path.ParsePathReturnValueSuccess;

import java.util.List;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 13:10
 */
public class ParseManager {
    private final FileSystemManager fileSystemManager;
    private final CommandManager commandManager;

    public ParseManager(FileSystemManager fileSystemManager, CommandManager commandManager) {
        this.fileSystemManager = fileSystemManager;
        this.commandManager = commandManager;
    }

    public ParseReturnValue parse(List<String> commandLine, ShellDirectory currentDirectory) {

        // The first arg is always the command.
        final String commandArg = commandLine.get(0);

        // Parse the command.
        final ParsePathReturnValue parseCommandReturnValue = fileSystemManager.parsePathToCommand(commandArg, currentDirectory);
        if (parseCommandReturnValue.isFailure()) {
            // Failed to parse the command.
            return ParseReturnValue.failureFrom(parseCommandReturnValue.getFailure());
        }

        final ParsePathReturnValueSuccess parseCommandSuccess = parseCommandReturnValue.getSuccess();
        final List<ShellDirectory> path = parseCommandSuccess.getPath();
        final ShellCommand command = (ShellCommand) parseCommandSuccess.getEntry();

        // Parse the command args.
        // The args start from the 2nd commandLine element (the first was the command).
        final List<String> args = commandLine.subList(1, commandLine.size());
        final ParseCommandArgsReturnValue parseArgsReturnValue = commandManager.parseArgs(command, args);
        if (parseArgsReturnValue.isFailure()) {
            return ParseReturnValue.failureFrom(parseArgsReturnValue.getFailure());
        }

        final CommandArgs parsedArgs = parseArgsReturnValue.getSuccess().getArgs();
        return ParseReturnValue.success(path, command, parsedArgs);
    }
}
