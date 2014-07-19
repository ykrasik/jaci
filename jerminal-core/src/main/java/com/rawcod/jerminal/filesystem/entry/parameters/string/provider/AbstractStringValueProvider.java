package com.rawcod.jerminal.filesystem.entry.parameters.string.provider;

import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.filesystem.entry.ShellAutoComplete;
import com.rawcod.jerminal.shell.parser.ShellStringParser;
import com.rawcod.jerminal.returnvalue.autocomplete.flow.AutoCompleteReturnValue;

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
    public AutoCompleteReturnValue autoComplete(String arg) {
        final ShellStringParser<String> possibleValues = getPossibleValues();
        if (possibleValues.isEmpty()) {
            // No possible values defined, every value is possible.
            final String errorMessage = String.format("No possible values defined for param '%s'", name);
            return AutoCompleteReturnValue.failureInvalidArgument(errorMessage, ShellAutoComplete.none());
        }
        return possibleValues.autoComplete(arg);
    }

    @Override
    public ParseReturnValue<?> parse(String arg) {
        final ShellStringParser<String> possibleValues = getPossibleValues();
        if (possibleValues.isEmpty()) {
            // No possible values defined, every value is possible.
            return ParseReturnValue.success(arg);
        }
        return possibleValues.parse(arg);
    }

    protected abstract ShellStringParser<String> getPossibleValues();
}
