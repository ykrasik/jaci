package com.rawcod.jerminal.command;

import com.rawcod.jerminal.returnvalue.execute.executor.ExecutorReturnValue;

/**
 * User: ykrasik
 * Date: 04/01/14
 */
public abstract class CommandExecutor {
    public abstract ExecutorReturnValue execute(CommandArgs args, ExecutionContext context);

    protected ExecutorReturnValue success() {
        return ExecutorReturnValue.success();
    }

    protected ExecutorReturnValue success(Object returnValue) {
        return ExecutorReturnValue.success(returnValue);
    }

    protected ExecutorReturnValue failure(String message) {
        return ExecutorReturnValue.failure(message);
    }

    protected ExecutorReturnValue failure(String format, Object... args) {
        return ExecutorReturnValue.failure(format, args);
    }

    protected ExecutorReturnValue failure(Exception e, String message) {
        return ExecutorReturnValue.failure(e, message);
    }

    protected ExecutorReturnValue failure(Exception e, String format, Object... args) {
        return ExecutorReturnValue.failure(e, format, args);
    }
}
