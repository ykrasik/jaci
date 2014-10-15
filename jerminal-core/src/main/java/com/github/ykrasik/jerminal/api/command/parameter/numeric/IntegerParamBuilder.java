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

package com.github.ykrasik.jerminal.api.command.parameter.numeric;

import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.internal.command.parameter.ParamUtils;
import com.github.ykrasik.jerminal.internal.command.parameter.optional.OptionalParam;
import com.google.common.base.Supplier;

import java.util.Objects;

/**
 * A builder for an {@link IntegerParam}.<br>
 * By default creates mandatory parameters, but can create optional parameters via
 * {@link #setOptional(Integer)} and {@link #setOptional(Supplier)}.
 *
 * @author Yevgeny Krasik
 */
public class IntegerParamBuilder {
    private final String name;
    private String description = "int parameter";
    private Supplier<Integer> defaultValueSupplier;

    public IntegerParamBuilder(String name) {
        this.name = Objects.requireNonNull(name);
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
        return setOptional(ParamUtils.constValueSupplier(defaultValue));
    }

    public IntegerParamBuilder setOptional(Supplier<Integer> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
        return this;
    }
}
