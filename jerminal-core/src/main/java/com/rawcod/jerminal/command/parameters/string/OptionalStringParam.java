package com.rawcod.jerminal.command.parameters.string;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.parameters.ParamParseContext;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 19:44
 */
public class OptionalStringParam extends StringParam {
    private final Supplier<String> defaultValueSupplier;

    public OptionalStringParam(String name,
                               String description,
                               List<String> possibleValues,
                               Supplier<String> defaultValueSupplier) {
        super(name, description, possibleValues);
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
}
