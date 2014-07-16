package com.rawcod.jerminal.shell.entry.parameters.string.provider;

import com.rawcod.jerminal.shell.entry.ShellAutoComplete;
import com.rawcod.jerminal.shell.parser.ShellStringParser;
import com.rawcod.jerminal.shell.returnvalue.ShellAutoCompleteReturnValue;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

/**
 * User: ykrasik
 * Date: 25/01/14
 */
public abstract class AbstractStringValueProvider implements StringShellParamValueProvider {
    protected final String name;

    protected AbstractStringValueProvider(String name) {
        this.name = name;
    }

    @Override
    public ShellAutoCompleteReturnValue autoComplete(String arg) {
        final ShellStringParser<String> possibleValues = getPossibleValues();
        if (possibleValues.isEmpty()) {
            // No possible values defined, every value is possible.
            final String errorMessage = String.format("No possible values defined for param '%s'", name);
            return ShellAutoCompleteReturnValue.failureInvalidArgument(errorMessage, ShellAutoComplete.none());
        }
        return possibleValues.autoComplete(arg);
    }

    @Override
    public ShellParseReturnValue<?> parse(String arg) {
        final ShellStringParser<String> possibleValues = getPossibleValues();
        if (possibleValues.isEmpty()) {
            // No possible values defined, every value is possible.
            return ShellParseReturnValue.success(arg);
        }
        return possibleValues.parse(arg);
    }

    protected abstract ShellStringParser<String> getPossibleValues();
}
