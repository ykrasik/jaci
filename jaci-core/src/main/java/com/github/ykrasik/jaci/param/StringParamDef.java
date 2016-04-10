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

package com.github.ykrasik.jaci.param;

import com.github.ykrasik.jaci.Identifier;
import com.github.ykrasik.jaci.util.function.MoreSuppliers;
import com.github.ykrasik.jaci.util.function.Spplr;
import com.github.ykrasik.jaci.util.opt.Opt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A boolean parameter definition.
 * Built through the {@link StringParamDef.Builder} builder.<br>
 * <br>
 * String parameters can be constrained to only accept certain values. There are 3 types of constraints:
 * <ol>
 *     <li>None - All values are accepted. This is the default, applies if the accepted values list is empty.</li>
 *     <li>Static - Only pre-defined values are accepted. Can be set through {@link StringParamDef.Builder#setStaticValues(List)}.</li>
 *     <li>Dynamic - The acceptable values are calculated at runtime, by invoking a {@link Spplr}.
 *                   Can be set through {@link StringParamDef.Builder#setDynamicValues(Spplr)}.</li>
 * </ol>
 *
 * @author Yevgeny Krasik
 */
public class StringParamDef extends AbstractParamDef<String> {
    private final Spplr<List<String>> valuesSupplier;

    private StringParamDef(Identifier identifier,
                           Opt<Spplr<String>> defaultValueSupplier,
                           boolean nullable,
                           Spplr<List<String>> valuesSupplier) {
        super(identifier, defaultValueSupplier, nullable);
        this.valuesSupplier = Objects.requireNonNull(valuesSupplier, "valuesSupplier");
    }

    @Override
    public <E> E resolve(ParamDefResolver<E> resolver) {
        return resolver.stringParam(this);
    }

    /**
     * @return The accepted values {@link Spplr}. If the {@link List} returned by the supplier is empty, all values
     *         are accepted.
     */
    public Spplr<List<String>> getValuesSupplier() {
        return valuesSupplier;
    }

    /**
     * A builder for a {@link StringParamDef}.
     */
    public static class Builder {
        private static final Spplr<List<String>> NO_VALUES_SUPPLIER = MoreSuppliers.of(Collections.<String>emptyList());

        private final String name;
        private String description = "string";
        private Opt<Spplr<String>> defaultValueSupplier = Opt.absent();
        private boolean nullable;
        private Spplr<List<String>> valuesSupplier = NO_VALUES_SUPPLIER;

        /**
         * @param name Parameter name.
         */
        public Builder(String name) {
            this.name = Objects.requireNonNull(name, "name");
        }

        /**
         * @param description Parameter description.
         * @return {@code this}, for chaining.
         */
        public Builder setDescription(String description) {
            this.description = Objects.requireNonNull(description, "description");
            return this;
        }

        /**
         * Set this parameter to be optional with the given default value.
         *
         * @param defaultValue Constant value to return if the parameter isn't passed.
         * @return {@code this}, for chaining.
         */
        public Builder setOptional(String defaultValue) {
            return setOptional(MoreSuppliers.of(Objects.requireNonNull(defaultValue, "defaultValue")));
        }

        /**
         * Set this parameter to be optional with a default value supplied by then given {@link Spplr}.
         *
         * @param defaultValueSupplier Supplier to invoke if the parameter isn't passed.
         * @return {@code this}, for chaining.
         */
        public Builder setOptional(Spplr<String> defaultValueSupplier) {
            this.defaultValueSupplier = Opt.of(defaultValueSupplier);
            return this;
        }

        /**
         * @param nullable Whether this parameter accepts {@code null} values.
         * @return {@code this}, for chaining.
         */
        public Builder setNullable(boolean nullable) {
            this.nullable = nullable;
            return this;
        }

        /**
         * Set this parameter to only accept a pre-defined array of values.
         * If the values array is empty, all values will be accepted.
         *
         * @param values Values this parameter can accept. If empty, all values will be accepted.
         * @return {@code this}, for chaining.
         */
        public Builder setStaticValues(String... values) {
            return setStaticValues(Arrays.asList(values));
        }

        /**
         * Set this parameter to only accept a pre-defined {@link List} of values.
         * If the values list is empty, all values will be accepted.
         *
         * @param values Values this parameter can accept. If empty, all values will be accepted.
         * @return {@code this}, for chaining.
         */
        public Builder setStaticValues(List<String> values) {
            this.valuesSupplier = MoreSuppliers.of(Objects.requireNonNull(values, "values"));
            return this;
        }

        /**
         * Set this parameter to only accept a set of values that is calculated at runtime, supplied by the {@link Spplr}.
         * If the supplier returns an empty values list, all values will be accepted.
         *
         * @param valuesSupplier {@link Spplr} to invoke for the list of acceptable values.
         *                       If the returned list is empty, all values will be accepted.
         * @return {@code this}, for chaining.
         */
        public Builder setDynamicValues(Spplr<List<String>> valuesSupplier) {
            this.valuesSupplier = Objects.requireNonNull(valuesSupplier, "valuesSupplier");
            return this;
        }

        /**
         * @return A {@link StringParamDef} built out of this builder's parameters.
         */
        public StringParamDef build() {
            final Identifier identifier = new Identifier(name, description);
            return new StringParamDef(identifier, defaultValueSupplier, nullable, valuesSupplier);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Builder{");
            sb.append("name='").append(name).append('\'');
            sb.append(", description='").append(description).append('\'');
            sb.append(", defaultValueSupplier=").append(defaultValueSupplier);
            sb.append(", nullable=").append(nullable);
            sb.append(", valuesSupplier=").append(valuesSupplier);
            sb.append('}');
            return sb.toString();
        }
    }
}
