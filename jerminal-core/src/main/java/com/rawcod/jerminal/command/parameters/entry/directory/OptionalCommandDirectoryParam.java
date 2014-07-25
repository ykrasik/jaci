package com.rawcod.jerminal.command.parameters.entry.directory;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.parameters.ParamParseContext;
import com.rawcod.jerminal.filesystem.entry.directory.ShellDirectory;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

/**
 * User: ykrasik
 * Date: 18/01/14
 */
public class OptionalCommandDirectoryParam extends CommandDirectoryParam  {
    private final Supplier<ShellDirectory> defaultValueSupplier;

    public OptionalCommandDirectoryParam(String name, String description, Supplier<ShellDirectory> defaultValueSupplier) {
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
        return String.format("[%s: directory]", getName());
    }
}
