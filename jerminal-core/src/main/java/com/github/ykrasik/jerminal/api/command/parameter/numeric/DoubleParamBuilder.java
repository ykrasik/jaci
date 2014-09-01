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

import com.google.common.base.Supplier;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.internal.command.parameter.ParamUtils;
import com.github.ykrasik.jerminal.internal.command.parameter.optional.OptionalParam;

/**
 * A builder for a {@link DoubleParam}.<br>
 * By default creates mandatory parameters, but can be set to create optional parameters via
 * {@link #setOptional(Double)} and {@link #setOptional(Supplier)}.
 *
 * @author Yevgeny Krasik
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
        return setOptional(ParamUtils.constValueSupplier(defaultValue));
    }

    public DoubleParamBuilder setOptional(Supplier<Double> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
        return this;
    }
}