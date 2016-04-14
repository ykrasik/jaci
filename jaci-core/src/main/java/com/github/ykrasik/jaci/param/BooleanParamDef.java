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
 * A boolean parameter definition.
 * Built through the {@link BooleanParamDef.Builder} builder.
 *
 * @author Yevgeny Krasik
 */
public class BooleanParamDef extends AbstractParamDef<Boolean> {
    private BooleanParamDef(Identifier identifier, Opt<Spplr<Boolean>> defaultValueSupplier, boolean nullable) {
        super(identifier, defaultValueSupplier, nullable);
    }

    @Override
    public <E> E resolve(ParamDefResolver<E> resolver) {
        return resolver.booleanParam(this);
    }

    /**
     * A builder for a {@link BooleanParamDef}.
     */
    public static class Builder {
        private final String name;
        private String description = "boolean";
        private Opt<Spplr<Boolean>> defaultValueSupplier = Opt.absent();
        private boolean nullable = false;

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
        public Builder setOptional(boolean defaultValue) {
            return setOptional(MoreSuppliers.of(defaultValue));
        }

        /**
         * Set this parameter to be optional with a default value supplied by then given {@link Spplr}.
         *
         * @param defaultValueSupplier Supplier to invoke if the parameter isn't passed.
         * @return {@code this}, for chaining.
         */
        public Builder setOptional(Spplr<Boolean> defaultValueSupplier) {
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
         * @return A {@link BooleanParamDef} built out of this builder's parameters.
         */
        public BooleanParamDef build() {
            final Identifier identifier = new Identifier(name, description);
            return new BooleanParamDef(identifier, defaultValueSupplier, nullable);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Builder{");
            sb.append("name='").append(name).append('\'');
            sb.append(", description='").append(description).append('\'');
            sb.append(", defaultValueSupplier=").append(defaultValueSupplier);
            sb.append(", nullable=").append(nullable);
            sb.append('}');
            return sb.toString();
        }
    }
}
