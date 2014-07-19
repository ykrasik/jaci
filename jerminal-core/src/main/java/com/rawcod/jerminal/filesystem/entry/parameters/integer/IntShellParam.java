package com.rawcod.jerminal.filesystem.entry.parameters.integer;

import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.command.param.ShellParam;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue;

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
    public AutoCompleteReturnValue autoComplete(String arg) {
        // Integers cannot be auto-completed.
        final String errorMessage = String.format("Cannot auto-complete integer param '%s'", name);
        return AutoCompleteReturnValue.failureInvalidArgument(errorMessage);
    }

    @Override
    public ParseReturnValue<Integer> parse(String arg) {
        if (arg == null || arg.isEmpty()) {
            return ParseReturnValue.failureMissingArgument(name);
        }

        try {
            final Integer parsedValue = Integer.parseInt(arg);
            return ParseReturnValue.success(parsedValue);
        } catch (NumberFormatException ignored) {
            final String errorMessage = String.format("Error parsing parameter '%s' - Invalid number: '%s'", name, arg);
            return ParseReturnValue.failureInvalidArgument(errorMessage);
        }
    }

    @Override
    public String toString() {
        return String.format("{%s: int}", name);
    }
}
