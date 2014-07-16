package com.rawcod.jerminal.shell.entry.command;

import com.rawcod.jerminal.shell.returnvalue.ShellExecuteReturnValue;

import java.util.Set;

/**
 * User: ykrasik
 * Date: 04/01/14
 */
public abstract class ShellCommandExecutor {
    public final ShellExecuteReturnValue execute(ShellCommandArgs args, Set<String> flags) {
        try {
            return doExecute(args, flags);
        } catch (Exception e) {
            return failure("Error running command: '%s'", e);
        }
    }

    protected abstract ShellExecuteReturnValue doExecute(ShellCommandArgs args, Set<String> flags);

    protected ShellExecuteReturnValue successNoMessage() {
        return ShellExecuteReturnValue.successNoMessage();
    }

    protected ShellExecuteReturnValue success(String message) {
        return ShellExecuteReturnValue.success(message);
    }

    protected ShellExecuteReturnValue success(String format, Object arg) {
        return ShellExecuteReturnValue.success(format, arg);
    }

    protected ShellExecuteReturnValue success(String format, Object arg1, Object arg2) {
        return ShellExecuteReturnValue.success(format, arg1, arg2);
    }

    protected ShellExecuteReturnValue success(String format, Object arg1, Object arg2, Object arg3) {
        return ShellExecuteReturnValue.success(format, arg1, arg2, arg3);
    }

    protected ShellExecuteReturnValue failure(String message) {
        return ShellExecuteReturnValue.failure(message);
    }

    protected ShellExecuteReturnValue failure(String format, Object arg) {
        return ShellExecuteReturnValue.failure(format, arg);
    }

    protected ShellExecuteReturnValue failure(String format, Object arg1, Object arg2) {
        return ShellExecuteReturnValue.failure(format, arg1, arg2);
    }

    protected ShellExecuteReturnValue failure(String format, Object arg1, Object arg2, Object arg3) {
        return ShellExecuteReturnValue.failure(format, arg1, arg2, arg3);
    }
}
