package com.rawcod.jerminal.command.parameters.entry.file;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.parameters.ParamParseContext;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 20:39
 */
public class OptionalFileParam extends FileParam {
    private final Supplier<ShellCommand> defaultValueSupplier;

    public OptionalFileParam(String name, String description, Supplier<ShellCommand> defaultValueSupplier) {
        super(name, description);
        this.defaultValueSupplier = defaultValueSupplier;
    }

    @Override
    public boolean isOptional() {
        return true;
    }

    @Override
    public ParseParamValueReturnValue parse(Optional<String> rawValue, ParamParseContext context) {
        if (rawValue.isPresent()) {
            return parse(rawValue.get(), context);
        }

        return ParseParamValueReturnValue.success(defaultValueSupplier.get());
    }

    @Override
    public String toString() {
        return String.format("[%s: file]", getName());
    }
}
