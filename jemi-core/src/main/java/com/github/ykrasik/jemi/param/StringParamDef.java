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

package com.github.ykrasik.jemi.param;

import com.github.ykrasik.jemi.Identifier;
import com.github.ykrasik.jemi.util.function.Supplier;
import com.github.ykrasik.jemi.util.function.Suppliers;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A boolean parameter definition.
 * Built through the {@link StringParamDef.Builder} builder.<br>
 * <br>
 * String parameters can be constrained to only accept certain values. There are 3 types of constraints:
 * <ol>
 *     <li>None - All values are accepted. This is the default, applies if the accepted values list is empty.</li>
 *     <li>Static - Only pre-defined values are accepted. Can be set through {@link StringParamDef.Builder#setStaticValues(List)}.</li>
 *     <li>Dynamic - The acceptable values are calculated at runtime, by invoking a {@link Supplier}.
 *                   Can be set through {@link StringParamDef.Builder#setDynamicValues(Supplier)}.</li>
 * </ol>
 *
 * @author Yevgeny Krasik
 */
@EqualsAndHashCode(callSuper = true)
public class StringParamDef extends AbstractParamDef<String> {
    private final Supplier<List<String>> valuesSupplier;

    private StringParamDef(Identifier identifier, Opt<Supplier<String>> defaultValueSupplier, @NonNull Supplier<List<String>> valuesSupplier) {
        super(identifier, defaultValueSupplier);
        this.valuesSupplier = valuesSupplier;
    }

    @Override
    public <E> E resolve(ParamDefResolver<E> resolver) {
        return resolver.stringParam(this);
    }

    /**
     * @return The accepted values {@link Supplier}. If the {@link List} returned by the supplier is empty, all values
     *         are accepted.
     */
    public Supplier<List<String>> getValuesSupplier() {
        return valuesSupplier;
    }

    /**
     * A builder for a {@link StringParamDef}.
     */
    @ToString
    public static class Builder {
        private static final Supplier<List<String>> NO_VALUES_SUPPLIER = Suppliers.of(Collections.<String>emptyList());

        private final String name;
        private String description = "string";
        private Opt<Supplier<String>> defaultValueSupplier = Opt.absent();
        private Supplier<List<String>> valuesSupplier = NO_VALUES_SUPPLIER;

        /**
         * @param name Parameter name.
         */
        public Builder(@NonNull String name) {
            this.name = name;
        }

        /**
         * @param description Parameter description.
         * @return {@code this}, for chaining.
         */
        public Builder setDescription(@NonNull String description) {
            this.description = description;
            return this;
        }

        /**
         * Set this parameter to be optional, and return the given constant value if it is not passed.
         *
         * @param defaultValue Constant value to return if the parameter isn't passed.
         * @return {@code this}, for chaining.
         */
        public Builder setOptional(@NonNull String defaultValue) {
            return setOptional(Suppliers.of(defaultValue));
        }

        /**
         * Set this parameter to be optional, and invoke the given {@link Supplier} for a default value if it is not passed.
         *
         * @param defaultValueSupplier Supplier to invoke if the parameter isn't passed.
         * @return {@code this}, for chaining.
         */
        public Builder setOptional(@NonNull Supplier<String> defaultValueSupplier) {
            this.defaultValueSupplier = Opt.of(defaultValueSupplier);
            return this;
        }

        /**
         * Set this parameter to only accept a pre-defined array of values.
         * If the values array is empty, all values will be accepted.
         *
         * @param values Values this parameter can accept. If empty, all values will be accepted.
         * @return {@code this}, for chaining.
         */
        public Builder setStaticValues(@NonNull String... values) {
            return setStaticValues(Arrays.asList(values));
        }

        /**
         * Set this parameter to only accept a pre-defined {@link List} of values.
         * If the values list is empty, all values will be accepted.
         *
         * @param values Values this parameter can accept. If empty, all values will be accepted.
         * @return {@code this}, for chaining.
         */
        public Builder setStaticValues(@NonNull List<String> values) {
            this.valuesSupplier = Suppliers.of(values);
            return this;
        }

        /**
         * Set this parameter to only accept a set of values that is calculated at runtime, supplied by the {@link Supplier}.
         * If the supplier returns an empty values list, all values will be accepted.
         *
         * @param valuesSupplier {@link Supplier} to invoke for the list of acceptable values.
         *                       If the returned list is empty, all values will be accepted.
         * @return {@code this}, for chaining.
         */
        public Builder setDynamicValues(@NonNull Supplier<List<String>> valuesSupplier) {
            this.valuesSupplier = valuesSupplier;
            return this;
        }

        /**
         * @return A {@link StringParamDef} built out of this builder's parameters.
         */
        public StringParamDef build() {
            final Identifier identifier = new Identifier(name, description);
            return new StringParamDef(identifier, defaultValueSupplier, valuesSupplier);
        }
    }
}
