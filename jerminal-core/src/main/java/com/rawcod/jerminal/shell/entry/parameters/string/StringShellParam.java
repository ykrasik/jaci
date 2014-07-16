package com.rawcod.jerminal.shell.entry.parameters.string;

import com.rawcod.jerminal.shell.ShellManager;
import com.rawcod.jerminal.shell.entry.parameters.ShellParam;
import com.rawcod.jerminal.shell.entry.parameters.string.provider.ConstStringValueProvider;
import com.rawcod.jerminal.shell.entry.parameters.string.provider.StringShellParamValueProvider;
import com.rawcod.jerminal.shell.returnvalue.ShellAutoCompleteReturnValue;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

import java.util.Arrays;
import java.util.List;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class StringShellParam implements ShellParam {
    protected final String name;
    private final StringShellParamValueProvider valueProvider;

    public StringShellParam(String name, String... possibleValues) {
        this(name, Arrays.asList(possibleValues));
    }

    public StringShellParam(String name, List<String> possibleValues) {
        this(name, new ConstStringValueProvider(name, possibleValues));
    }

    public StringShellParam(String name, StringShellParamValueProvider valueProvider) {
        this.name = name;
        this.valueProvider = valueProvider;
    }

    @Override
    public void install(ShellManager manager) {
        // Nothing to do here
    }

    @Override
    public ShellAutoCompleteReturnValue autoComplete(String arg) {
        return valueProvider.autoComplete(arg);
    }

    @Override
    public ShellParseReturnValue<?> parse(String arg) {
        if (arg == null) {
            return ShellParseReturnValue.failureMissingArgument(name);
        }

        return valueProvider.parse(arg);
    }

    @Override
    public String toString() {
        return String.format("{%s: String}", name);
    }
}
