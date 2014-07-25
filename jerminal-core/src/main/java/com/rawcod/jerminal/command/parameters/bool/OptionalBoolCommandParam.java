package com.rawcod.jerminal.command.parameters.bool;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.parameters.ParamParseContext;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class OptionalBoolCommandParam extends BoolCommandParam {
    private final Supplier<Boolean> defaultValueSupplier;

    public OptionalBoolCommandParam(String name, String description, Supplier<Boolean> defaultValueSupplier) {
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
        return String.format("[%s: bool]", getName());
    }
}
