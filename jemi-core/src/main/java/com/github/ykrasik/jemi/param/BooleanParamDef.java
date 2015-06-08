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
import com.github.ykrasik.jemi.util.function.Spplr;
import com.github.ykrasik.jemi.util.function.MoreSuppliers;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.NonNull;
import lombok.ToString;

/**
 * A boolean parameter definition.
 * Built through the {@link BooleanParamDef.Builder} builder.
 *
 * @author Yevgeny Krasik
 */
public class BooleanParamDef extends AbstractParamDef<Boolean> {
    private BooleanParamDef(Identifier identifier, Opt<Spplr<Boolean>> defaultValueSupplier) {
        super(identifier, defaultValueSupplier);
    }

    @Override
    public <E> E resolve(ParamDefResolver<E> resolver) {
        return resolver.booleanParam(this);
    }

    /**
     * A builder for a {@link BooleanParamDef}.
     */
    @ToString
    public static class Builder {
        private final String name;
        private String description = "boolean";
        private Opt<Spplr<Boolean>> defaultValueSupplier = Opt.absent();

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
        public Builder setOptional(boolean defaultValue) {
            return setOptional(MoreSuppliers.of(defaultValue));
        }

        /**
         * Set this parameter to be optional, and invoke the given {@link Spplr} for a default value if it is not passed.
         *
         * @param defaultValueSupplier Supplier to invoke if the parameter isn't passed.
         * @return {@code this}, for chaining.
         */
        public Builder setOptional(@NonNull Spplr<Boolean> defaultValueSupplier) {
            this.defaultValueSupplier = Opt.of(defaultValueSupplier);
            return this;
        }

        /**
         * @return A {@link BooleanParamDef} built out of this builder's parameters.
         */
        public BooleanParamDef build() {
            final Identifier identifier = new Identifier(name, description);
            return new BooleanParamDef(identifier, defaultValueSupplier);
        }
    }
}
