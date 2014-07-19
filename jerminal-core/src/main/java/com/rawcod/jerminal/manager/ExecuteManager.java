package com.rawcod.jerminal.manager;

import com.rawcod.jerminal.command.args.CommandArgs;
import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValueSuccess;

import java.util.List;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 12:34
 */
public class ExecuteManager {
    private final ParseManager parseManager;

    public ExecuteManager(ParseManager parseManager) {
        this.parseManager = parseManager;
    }

    public ExecuteReturnValue execute(List<String> commandLine, ShellDirectory currentDirectory) {
        // Parse commandLine.
        final ParseReturnValue parseReturnValue = parseManager.parse(commandLine, currentDirectory);
        if (parseReturnValue.isFailure()) {
            return ExecuteReturnValue.failureFrom(parseReturnValue.getFailure());
        }

        final ParseReturnValueSuccess parseSuccess = parseReturnValue.getSuccess();
        final ShellCommand command = parseSuccess.getCommand();
        final CommandArgs args = parseSuccess.getArgs();

        // Execute command.
        final CommandExecutor executor = command.getExecutor();
        return executor.execute(args);
    }
}
