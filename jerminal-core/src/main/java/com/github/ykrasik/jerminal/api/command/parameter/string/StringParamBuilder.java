/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.api.command.parameter.string;

import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.internal.command.parameter.ParamUtils;
import com.github.ykrasik.jerminal.internal.command.parameter.optional.OptionalParam;
import com.google.common.base.Supplier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A builder for a {@link StringParam}.<br>
 * By default creates mandatory parameters, but can create optional parameters via
 * {@link #setOptional(String)} and {@link #setOptional(Supplier)}.<br>
 * By default creates parameters that accept any string, but can be set to create parameters that are
 * constrained to a pre-defined set of strings with {@link #setConstantAcceptableValues(List)} and {@link #setConstantAcceptableValues(List)},
 * or to a dynamic set of strings that is supplied at runtime with {@link #setDynamicAcceptableValuesSupplier(Supplier)}.
 *
 * @author Yevgeny Krasik
 */
public class StringParamBuilder {
    private static final Supplier<Trie<String>> NO_VALUES_SUPPLIER = ParamUtils.constStringValuesSupplier(Collections.<String>emptyList());

    private final String name;
    private String description = "string parameter";
    private Supplier<Trie<String>> acceptableValuesSupplier = NO_VALUES_SUPPLIER;
    private Supplier<String> defaultValueSupplier;

    public StringParamBuilder(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public CommandParam build() {
        final CommandParam param = new StringParam(name, description, acceptableValuesSupplier);
        if (defaultValueSupplier == null) {
            return param;
        }
        return new OptionalParam<>(param, defaultValueSupplier);
    }

    public StringParamBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public StringParamBuilder setConstantAcceptableValues(String... possibleValues) {
        return setConstantAcceptableValues(Arrays.asList(possibleValues));
    }

    public StringParamBuilder setConstantAcceptableValues(List<String> possibleValues) {
        this.acceptableValuesSupplier = ParamUtils.constStringValuesSupplier(possibleValues);
        return this;
    }

    public StringParamBuilder setDynamicAcceptableValuesSupplier(Supplier<List<String>> supplier) {
        this.acceptableValuesSupplier = ParamUtils.dynamicStringValuesSupplier(supplier);
        return this;
    }

    public StringParamBuilder setOptional(String defaultValue) {
        return setOptional(ParamUtils.constValueSupplier(defaultValue));
    }

    public StringParamBuilder setOptional(Supplier<String> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
        return this;
    }
}
