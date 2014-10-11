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

package com.github.ykrasik.jerminal.api.command.parameter.bool;

import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.internal.command.parameter.ParamUtils;
import com.github.ykrasik.jerminal.internal.command.parameter.optional.OptionalParam;
import com.google.common.base.Supplier;

import java.util.Objects;

/**
 * A builder for a {@link BooleanParam}.<br>
 * By default creates mandatory parameters, but can create optional parameters via
 * {@link #setOptional(Boolean)} and {@link #setOptional(Supplier)}.
 *
 * @author Yevgeny Krasik
 */
public class BooleanParamBuilder {
    private final String name;
    private String description = "boolean";
    private Supplier<Boolean> defaultValueSupplier;

    public BooleanParamBuilder(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public CommandParam build() {
        final CommandParam param = new BooleanParam(name, description);
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
        return setOptional(ParamUtils.constValueSupplier(defaultValue));
    }

    public BooleanParamBuilder setOptional(Supplier<Boolean> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
        return this;
    }
}
