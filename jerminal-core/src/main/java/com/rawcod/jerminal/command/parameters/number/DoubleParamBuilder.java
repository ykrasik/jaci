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
public class DoubleParamBuilder {
    private final String name;
    private String description = "double";
    private Supplier<Double> defaultValueSupplier;

    public DoubleParamBuilder(String name) {
        this.name = name;
    }

    public CommandParam build() {
        final CommandParam param = new DoubleParam(name, description);
        if (defaultValueSupplier == null) {
            return param;
        }
        return new OptionalParam<>(param, defaultValueSupplier);
    }

    public DoubleParamBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public DoubleParamBuilder setOptional(Double defaultValue) {
        return setOptional(Params.constValueSupplier(defaultValue));
    }

    public DoubleParamBuilder setOptional(Supplier<Double> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
        return this;
    }
}
