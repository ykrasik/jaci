package com.rawcod.jerminal.command.parameters.string;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.Params;
import com.rawcod.jerminal.command.parameters.optional.OptionalParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: ykrasik
 * Date: 28/07/2014
 * Time: 00:22
 */
public class StringParamBuilder {
    private final String name;
    private String description = "string";
    private final List<String> possibleValues;
    private Supplier<String> defaultValueSupplier;

    public StringParamBuilder(String name) {
        this.name = name;
        this.possibleValues = new ArrayList<>(4);
    }

    public CommandParam build() {
        final CommandParam param = new StringParam(name, description, possibleValues);
        if (defaultValueSupplier == null) {
            return param;
        }
        return new OptionalParam<>(param, defaultValueSupplier);
    }

    public StringParamBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public StringParamBuilder addPossibleValue(String possibleValue) {
        possibleValues.add(possibleValue);
        return this;
    }

    public StringParamBuilder addPossibleValues(String... possibleValues) {
        return addPossibleValues(Arrays.asList(possibleValues));
    }

    public StringParamBuilder addPossibleValues(List<String> possibleValues) {
        this.possibleValues.addAll(possibleValues);
        return this;
    }

    public StringParamBuilder setOptional(String defaultValue) {
        return setOptional(Params.constValueSupplier(defaultValue));
    }

    public StringParamBuilder setOptional(Supplier<String> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
        return this;
    }
}
