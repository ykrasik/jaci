package com.rawcod.jerminal.command;

import com.rawcod.jerminal.returnvalue.execute.ExecuteReturnValue;

/**
 * User: ykrasik
 * Date: 04/01/14
 */
public abstract class CommandExecutor {
    public abstract ExecuteReturnValue execute(CommandArgs args, ExecutionContext context);

    protected ExecuteReturnValue successNoMessage() {
        return ExecuteReturnValue.successNoMessage();
    }

    protected ExecuteReturnValue success(String message) {
        return ExecuteReturnValue.success(message);
    }

    protected ExecuteReturnValue success(String format, Object arg) {
        return ExecuteReturnValue.success(format, arg);
    }

    protected ExecuteReturnValue success(String format, Object arg1, Object arg2) {
        return ExecuteReturnValue.success(format, arg1, arg2);
    }

    protected ExecuteReturnValue success(String format, Object arg1, Object arg2, Object arg3) {
        return ExecuteReturnValue.success(format, arg1, arg2, arg3);
    }

    protected ExecuteReturnValue failure(String message) {
        return ExecuteReturnValue.failure(message);
    }

    protected ExecuteReturnValue failure(String format, Object arg) {
        return ExecuteReturnValue.failure(format, arg);
    }

    protected ExecuteReturnValue failure(String format, Object arg1, Object arg2) {
        return ExecuteReturnValue.failure(format, arg1, arg2);
    }

    protected ExecuteReturnValue failure(String format, Object arg1, Object arg2, Object arg3) {
        return ExecuteReturnValue.failure(format, arg1, arg2, arg3);
    }
}
