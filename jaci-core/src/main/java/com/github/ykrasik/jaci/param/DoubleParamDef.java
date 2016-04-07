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

import java.util.Objects;

/**
 * A double parameter definition.
 * Built through the {@link DoubleParamDef.Builder} builder.
 *
 * @author Yevgeny Krasik
 */
public class DoubleParamDef extends AbstractParamDef<Double> {
    private DoubleParamDef(Identifier identifier, Opt<Spplr<Double>> defaultValueSupplier) {
        super(identifier, defaultValueSupplier);
    }

    @Override
    public <E> E resolve(ParamDefResolver<E> resolver) {
        return resolver.doubleParam(this);
    }

    /**
     * A builder for a {@link DoubleParamDef}.
     */
    public static class Builder {
        private final String name;
        private String description = "double";
        private Opt<Spplr<Double>> defaultValueSupplier = Opt.absent();

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
        public Builder setOptional(double defaultValue) {
            return setOptional(MoreSuppliers.of(defaultValue));
        }

        /**
         * Set this parameter to be optional with a default value supplied by then given {@link Spplr}.
         *
         * @param defaultValueSupplier Supplier to invoke if the parameter isn't passed.
         * @return {@code this}, for chaining.
         */
        public Builder setOptional(Spplr<Double> defaultValueSupplier) {
            this.defaultValueSupplier = Opt.of(defaultValueSupplier);
            return this;
        }

        /**
         * @return A {@link DoubleParamDef} built out of this builder's parameters.
         */
        public DoubleParamDef build() {
            final Identifier identifier = new Identifier(name, description);
            return new DoubleParamDef(identifier, defaultValueSupplier);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Builder{");
            sb.append("name='").append(name).append('\'');
            sb.append(", description='").append(description).append('\'');
            sb.append(", defaultValueSupplier=").append(defaultValueSupplier);
            sb.append('}');
            return sb.toString();
        }
    }
}
