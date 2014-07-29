package com.rawcod.jerminal.command.parameters.bool;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.Params;
import com.rawcod.jerminal.command.parameters.entry.DirectoryParam;
import com.rawcod.jerminal.command.parameters.optional.OptionalParam;

/**
 * User: ykrasik
 * Date: 28/07/2014
 * Time: 00:22
 */
public class BooleanParamBuilder {
    private final String name;
    private String description = "boolean";
    private Supplier<Boolean> defaultValueSupplier;

    public BooleanParamBuilder(String name) {
        this.name = name;
    }

    public CommandParam build() {
        final CommandParam param = new DirectoryParam(name, description);
        if (defaultValueSupplier == null) {
            return param;
        }
        return new OptionalParam<>(param, defaultValueSupplier);
    }

    public BooleanParamBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public BooleanParamBuilder setOptional(Boolean defaultValue) {
        return setOptional(Params.constValueSupplier(defaultValue));
    }

    public BooleanParamBuilder setOptional(Supplier<Boolean> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
        return this;
    }
}
