/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jemi.core.param;

import com.github.ykrasik.jemi.core.Identifier;
import com.github.ykrasik.jemi.util.function.Supplier;
import com.github.ykrasik.jemi.util.function.Suppliers;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@EqualsAndHashCode(callSuper = true)
public class StringParamDef extends AbstractParamDef<String> {
    // TODO: Javadoc - this means that the values can be cached.
    @Getter
    private final boolean staticValues;

    @Getter
    private final Supplier<List<String>> valuesSupplier;

    private StringParamDef(Identifier identifier,
                           Opt<Supplier<String>> defaultValueSupplier,
                           boolean staticValues,
                           @NonNull Supplier<List<String>> valuesSupplier) {
        super(identifier, defaultValueSupplier);
        this.staticValues = staticValues;
        this.valuesSupplier = valuesSupplier;
    }

    @Override
    public <E> E resolve(ParamDefResolver<E> resolver) {
        return resolver.stringParam(this);
    }

    // TODO: JavaDoc
    @Accessors(chain = true)
    public static class Builder {
        private static final Supplier<List<String>> NO_VALUES_SUPPLIER = Suppliers.of(Collections.<String>emptyList());

        private final String name;

        @Setter
        @NonNull private String description = "string";

        private Opt<Supplier<String>> defaultValueSupplier = Opt.absent();

        private boolean staticValues = true;
        private Supplier<List<String>> valuesSupplier = NO_VALUES_SUPPLIER;

        public Builder(@NonNull String name) {
            this.name = name;
        }

        public Builder setOptional(@NonNull String defaultValue) {
            return setOptional(Suppliers.of(defaultValue));
        }

        public Builder setOptional(@NonNull Supplier<String> defaultValueSupplier) {
            this.defaultValueSupplier = Opt.of(defaultValueSupplier);
            return this;
        }

        public Builder setStaticValues(@NonNull String... values) {
            return setStaticValues(Arrays.asList(values));
        }

        public Builder setStaticValues(@NonNull List<String> values) {
            this.staticValues = true;
            this.valuesSupplier = Suppliers.of(values);
            return this;
        }

        public Builder setDynamicValues(@NonNull Supplier<List<String>> valuesSupplier) {
            this.staticValues = false;
            this.valuesSupplier = valuesSupplier;
            return this;
        }

        public StringParamDef build() {
            final Identifier identifier = new Identifier(name, description);
            return new StringParamDef(identifier, defaultValueSupplier, staticValues, valuesSupplier);
        }
    }
}
