package com.rawcod.jerminal.filesystem.entry.parameters.string;

import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.filesystem.entry.parameters.OptionalShellParam;
import com.rawcod.jerminal.filesystem.entry.parameters.ShellParamDefaultValueProvider;
import com.rawcod.jerminal.filesystem.entry.parameters.ShellParamDefaultValueProvider.Const;

import java.util.List;

/**
 * User: ykrasik
 * Date: 24/01/14
 */
public class OptionalStringShellParam extends StringShellParam implements OptionalShellParam {
    private final ShellParamDefaultValueProvider<String> defaultValueProvider;

    public OptionalStringShellParam(String name,
                                    List<String> possibleValues,
                                    ShellParamDefaultValueProvider<String> defaultValueProvider) {
        super(name, possibleValues);
        this.defaultValueProvider = defaultValueProvider;
    }

    public OptionalStringShellParam(String name, ShellParamDefaultValueProvider<String> defaultValueProvider, String... possibleValues) {
        super(name, possibleValues);
        this.defaultValueProvider = defaultValueProvider;
    }

    public OptionalStringShellParam(String name,
                                    List<String> possibleValues,
                                    String defaultValue) {
        super(name, possibleValues);
        this.defaultValueProvider = new Const<>(defaultValue);
    }

    public OptionalStringShellParam(String name, String defaultValue, String... possibleValues) {
        super(name, possibleValues);
        this.defaultValueProvider = new Const<>(defaultValue);
    }

    @Override
    public ParseReturnValue<?> parse(String arg) {
        if (arg != null) {
            return super.parse(arg);
        }

        // Value not provided, use default value
        final String defaultValue = defaultValueProvider.getDefaultValue();
        return ParseReturnValue.success(defaultValue);
    }

    @Override
    public String toString() {
        return String.format("[%s: string]", name);
    }
}
