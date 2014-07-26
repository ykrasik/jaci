package com.rawcod.jerminal.filesystem.entry.command;

import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.command.ExecutionContext;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.CommandParamManager;
import com.rawcod.jerminal.filesystem.entry.AbstractShellEntry;
import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValue;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 04/01/14
 */
public class ShellCommand extends AbstractShellEntry implements ReadOnlyCommand {
    private final CommandExecutor executor;
    private final CommandParamManager paramManager;

    public ShellCommand(String name,
                        String description,
                        List<CommandParam> params,
                        CommandExecutor executor) {
        super(name, description);

        this.executor = checkNotNull(executor, "executor is null!");
        this.paramManager = new CommandParamManager(params);
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public List<CommandParam> getParams() {
        return paramManager.getAllParams();
    }

    public CommandParamManager getParamManager() {
        return paramManager;
    }

    public ExecuteReturnValue execute(CommandArgs args) {
        final ExecutionContext context = new ExecutionContext();
        try {
            return doExecute(args, context);
        } catch (Exception e) {

        }
    }

    private ExecuteReturnValue doExecute(CommandArgs args, ExecutionContext context) {
        executor.execute(args, context);
    }
}
