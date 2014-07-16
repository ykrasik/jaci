package com.rawcod.jerminal.shell.entry.parameters.integer;

import com.rawcod.jerminal.shell.entry.parameters.OptionalShellParam;
import com.rawcod.jerminal.shell.entry.parameters.ShellParamDefaultValueProvider;
import com.rawcod.jerminal.shell.entry.parameters.ShellParamDefaultValueProvider.Const;
import com.rawcod.jerminal.shell.returnvalue.ShellParseReturnValue;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class OptionalIntShellParam extends IntShellParam implements OptionalShellParam {
    private final ShellParamDefaultValueProvider<Integer> defaultValueProvider;

    public OptionalIntShellParam(String name, ShellParamDefaultValueProvider<Integer> defaultValueProvider) {
        super(name);
        this.defaultValueProvider = defaultValueProvider;
    }

    public OptionalIntShellParam(String name, int defaultValueProvider) {
        super(name);
        this.defaultValueProvider = new Const<>(defaultValueProvider);
    }

    @Override
    public ShellParseReturnValue<Integer> parse(String arg) {
        if (arg != null) {
            return super.parse(arg);
        }

        // Value not provided, use default value
        final Integer defaultValue = defaultValueProvider.getDefaultValue();
        return ShellParseReturnValue.success(defaultValue);
    }

    @Override
    public String toString() {
        return String.format("[%s: int]", name);
    }
}
