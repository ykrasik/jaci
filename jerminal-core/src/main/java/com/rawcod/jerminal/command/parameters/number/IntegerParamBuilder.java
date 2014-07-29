package com.rawcod.jerminal.command.parameters.number;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.Params;
import com.rawcod.jerminal.command.parameters.optional.OptionalParam;

/**
 * User: ykrasik
 * Date: 28/07/2014
 * Time: 00:22
 */
public class IntegerParamBuilder {
    private final String name;
    private String description = "int";
    private Supplier<Integer> defaultValueSupplier;

    public IntegerParamBuilder(String name) {
        this.name = name;
    }

    public CommandParam build() {
        final CommandParam param = new IntegerParam(name, description);
        if (defaultValueSupplier == null) {
            return param;
        }
        return new OptionalParam<>(param, defaultValueSupplier);
    }

    public IntegerParamBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public IntegerParamBuilder setOptional(Integer defaultValue) {
        return setOptional(Params.constValueSupplier(defaultValue));
    }

    public IntegerParamBuilder setOptional(Supplier<Integer> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
        return this;
    }
}
