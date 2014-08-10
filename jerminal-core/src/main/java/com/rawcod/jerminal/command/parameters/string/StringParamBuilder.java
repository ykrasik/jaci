package com.rawcod.jerminal.command.parameters.string;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.command.parameters.Params;
import com.rawcod.jerminal.command.parameters.optional.OptionalParam;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: ykrasik
 * Date: 28/07/2014
 * Time: 00:22
 */
public class StringParamBuilder {
    private static final Supplier<Trie<String>> NO_VALUES_SUPPLIER = Params.constStringValuesSupplier(Collections.<String>emptyList());

    private final String name;
    private String description = "string";
    private Supplier<Trie<String>> possibleValuesSupplier = NO_VALUES_SUPPLIER;
    private Supplier<String> defaultValueSupplier;

    public StringParamBuilder(String name) {
        this.name = name;
    }

    public CommandParam build() {
        final CommandParam param = new StringParam(name, description, possibleValuesSupplier);
        if (defaultValueSupplier == null) {
            return param;
        }
        return new OptionalParam<>(param, defaultValueSupplier);
    }

    public StringParamBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public StringParamBuilder setConstantPossibleValues(String... possibleValues) {
        return setConstantPossibleValues(Arrays.asList(possibleValues));
    }

    public StringParamBuilder setConstantPossibleValues(List<String> possibleValues) {
        this.possibleValuesSupplier = Params.constStringValuesSupplier(possibleValues);
        return this;
    }

    public StringParamBuilder setDynamicPossibleValuesSupplier(Supplier<List<String>> supplier) {
        this.possibleValuesSupplier = Params.dynamicStringValuesSupplier(supplier);
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
