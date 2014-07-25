package com.rawcod.jerminal.command.parameters.integer;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.parameters.ParamParseContext;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class OptionalIntParam extends IntParam {
    private final Supplier<Integer> defaultValueSupplier;

    public OptionalIntParam(String name, String description, Supplier<Integer> defaultValueSupplier) {
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
        return String.format("[%s: int]", getName());
    }
}
