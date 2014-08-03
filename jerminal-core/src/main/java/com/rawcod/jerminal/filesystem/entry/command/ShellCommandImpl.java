package com.rawcod.jerminal.filesystem.entry.command;

import com.rawcod.jerminal.command.CommandArgs;
import com.rawcod.jerminal.command.CommandExecutor;
import com.rawcod.jerminal.command.ExecutionContext;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.manager.CommandParamManager;
import com.rawcod.jerminal.filesystem.entry.AbstractShellEntry;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.execute.executor.ExecutorReturnValue;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValue;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueFailure;
import com.rawcod.jerminal.returnvalue.execute.flow.ExecuteReturnValueSuccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 04/01/14
 */
public class ShellCommandImpl extends AbstractShellEntry implements ShellCommand {
    private final CommandExecutor executor;
    private final CommandParamManager paramManager;

    public ShellCommandImpl(String name,
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
    public ShellDirectory getDirectory() {
        final String message = String.format("'%s' is a command, not a directory!", getName());
        throw new IllegalStateException(message);
    }

    @Override
    public ShellCommand getCommand() {
        return this;
    }

    @Override
    public List<CommandParam> getParams() {
        return Collections.unmodifiableList(paramManager.getAllParams());
    }

    @Override
    public CommandParamManager getParamManager() {
        return paramManager;
    }

    @Override
    public ExecuteReturnValue execute(CommandArgs args) {
        final List<String> outputBuffer = new ArrayList<>();
        final ExecutionContext context = new ExecutionContext(outputBuffer);
        try {
            final ExecutorReturnValue returnValue = executor.execute(args, context);
            if (returnValue.isSuccess()) {
                context.println("Command '%s' executed successfully.", getName());
            } else {
                context.println("Command '%s' finished with an error: %s", getName(), returnValue.getFailure().getErrorMessage());
            }
            return translateReturnValue(returnValue, outputBuffer);
        } catch (Exception e) {
            context.println("Command '%s' was terminated by an unhandled exception: %s", getName(), e.getMessage());
            return ExecuteReturnValue.failureException(e, outputBuffer);
        }
    }

    private ExecuteReturnValue translateReturnValue(ExecutorReturnValue returnValue, List<String> outputBuffer) {
        if (returnValue.isSuccess()) {
            final ExecuteReturnValueSuccess success = returnValue.getSuccess();
            return ExecuteReturnValue.success(success.getReturnValue(), outputBuffer);
        } else {
            final ExecuteReturnValueFailure failure = returnValue.getFailure();
            return ExecuteReturnValue.failure(failure.getErrorMessage(), outputBuffer);
        }
    }
}
