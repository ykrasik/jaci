package com.rawcod.jerminal.filesystem.entry.parameters.bool;

import com.rawcod.jerminal.returnvalue.parse.flow.ParseReturnValue;
import com.rawcod.jerminal.filesystem.entry.parameters.OptionalShellParam;
import com.rawcod.jerminal.filesystem.entry.parameters.ShellParamDefaultValueProvider;
import com.rawcod.jerminal.filesystem.entry.parameters.ShellParamDefaultValueProvider.Const;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class OptionalBoolShellParam extends BoolShellParam implements OptionalShellParam {
    private final ShellParamDefaultValueProvider<Boolean> defaultValueProvider;

    public OptionalBoolShellParam(String name, ShellParamDefaultValueProvider<Boolean> defaultValueProvider) {
        super(name);
        this.defaultValueProvider = defaultValueProvider;
    }

    public OptionalBoolShellParam(String name, boolean defaultValueProvider) {
        super(name);
        this.defaultValueProvider = new Const<>(defaultValueProvider);
    }

//    @Override
//    public ShellAutoCompleteReturnValue autoComplete(String arg) {
//        final String argToAutoComplete = arg != null ? arg : "";
//        return super.autoComplete(argToAutoComplete);
//    }

    @Override
    public ParseReturnValue<Boolean> parse(String arg) {
        if (arg != null) {
            return super.parse(arg);
        }

        // Value not provided, use default value
        final Boolean defaultValue = defaultValueProvider.getDefaultValue();
        return ParseReturnValue.success(defaultValue);
    }

    @Override
    public String toString() {
        return String.format("[%s: bool]", name);
    }
}
