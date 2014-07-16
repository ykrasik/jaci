package com.rawcod.jerminal.shell.entry.parameters.integer;

import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.shell.entry.parameters.ShellParam;
import com.rawcod.jerminal.shell.returnvalue.ShellAutoCompleteReturnValue;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class IntShellParam implements ShellParam {
    protected final String name;

    public IntShellParam(String name) {
        this.name = name;
    }

    @Override
    public void install(ShellManager manager) {
        // Nothing to do here
    }

    @Override
    public ShellAutoCompleteReturnValue autoComplete(String arg) {
        // Integers cannot be auto-completed.
        final String errorMessage = String.format("Cannot auto-complete integer param '%s'", name);
        return ShellAutoCompleteReturnValue.failureInvalidArgument(errorMessage);
    }

    @Override
    public ShellParseReturnValue<Integer> parse(String arg) {
        if (arg == null || arg.isEmpty()) {
            return ShellParseReturnValue.failureMissingArgument(name);
        }

        try {
            final Integer parsedValue = Integer.parseInt(arg);
            return ShellParseReturnValue.success(parsedValue);
        } catch (NumberFormatException ignored) {
            final String errorMessage = String.format("Error parsing parameter '%s' - Invalid number: '%s'", name, arg);
            return ShellParseReturnValue.failureInvalidArgument(errorMessage);
        }
    }

    @Override
    public String toString() {
        return String.format("{%s: int}", name);
    }
}
