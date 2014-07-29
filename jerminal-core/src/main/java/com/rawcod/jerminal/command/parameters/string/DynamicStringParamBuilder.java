package com.rawcod.jerminal.command.parameters.string;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.Params;
import com.rawcod.jerminal.command.parameters.optional.OptionalParam;

import java.util.List;

/**
 * User: ykrasik
 * Date: 28/07/2014
 * Time: 00:22
 */
public class DynamicStringParamBuilder {
    private final String name;
    private String description = "string";
    private Supplier<List<String>> valuesSupplier;
    private Supplier<String> defaultValueSupplier;

    public DynamicStringParamBuilder(String name) {
        this.name = name;
    }

    public CommandParam build() {
        final CommandParam param = new DynamicStringParam(name, description, valuesSupplier);
        if (defaultValueSupplier == null) {
            return param;
        }
        return new OptionalParam<>(param, defaultValueSupplier);
    }

    public DynamicStringParamBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public DynamicStringParamBuilder setValuesSupplier(Supplier<List<String>> valuesSupplier) {
        this.valuesSupplier = valuesSupplier;
        return this;
    }

    public DynamicStringParamBuilder setOptional(String defaultValue) {
        return setOptional(Params.constValueSupplier(defaultValue));
    }

    public DynamicStringParamBuilder setOptional(Supplier<String> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
        return this;
    }
}
