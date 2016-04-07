/******************************************************************************
 * Copyright (C) 2016 Yevgeny Krasik                                          *
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
 * An enum parameter definition.
 * Built through the {@link EnumParamDef.Builder} builder.
 *
 * @author Yevgeny Krasik
 */
public class EnumParamDef<E extends Enum<E>> extends AbstractParamDef<E> {
    private final Class<E> enumClass;

    private EnumParamDef(Identifier identifier, Opt<Spplr<E>> defaultValueSupplier, Class<E> enumClass) {
        super(identifier, defaultValueSupplier);
        this.enumClass = Objects.requireNonNull(enumClass, "enumClass");
    }

    @Override
    public <T> T resolve(ParamDefResolver<T> resolver) {
        return resolver.enumParam(this);
    }

    /**
     * @return The {@code class} of the enum this paramDef represents.
     */
    public Class<E> getEnumClass() {
        return enumClass;
    }

    /**
     * A builder for a {@link EnumParamDef}.
     */
    public static class Builder<E extends Enum<E>> {
        private final Class<E> enumClass;
        private final String name;

        private String description = "enum";
        private Opt<Spplr<E>> defaultValueSupplier = Opt.absent();

        /**
         * @param enumClass Enum type.
         * @param name Parameter name.
         */
        public Builder(Class<E> enumClass, String name) {
            this.enumClass = Objects.requireNonNull(enumClass, "enumClass");
            this.name = Objects.requireNonNull(name, "name");
        }

        /**
         * @param description Parameter description.
         * @return {@code this}, for chaining.
         */
        public Builder<E> setDescription(String description) {
            this.description = Objects.requireNonNull(description, "description");
            return this;
        }

        /**
         * Set this parameter to be optional with the given default value.
         *
         * @param defaultValue Constant value to return if the parameter isn't passed.
         * @return {@code this}, for chaining.
         */
        public Builder<E> setOptional(E defaultValue) {
            return setOptional(MoreSuppliers.of(defaultValue));
        }

        /**
         * Set this parameter to be optional with a default value supplied by then given {@link Spplr}.
         *
         * @param defaultValueSupplier Supplier to invoke if the parameter isn't passed.
         * @return {@code this}, for chaining.
         */
        public Builder<E> setOptional(Spplr<E> defaultValueSupplier) {
            this.defaultValueSupplier = Opt.of(defaultValueSupplier);
            return this;
        }

        /**
         * @return An {@link EnumParamDef} built out of this builder's parameters.
         */
        public EnumParamDef<E> build() {
            final Identifier identifier = new Identifier(name, description);
            return new EnumParamDef<>(identifier, defaultValueSupplier, enumClass);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Builder{");
            sb.append("enumClass=").append(enumClass);
            sb.append(", name='").append(name).append('\'');
            sb.append(", description='").append(description).append('\'');
            sb.append(", defaultValueSupplier=").append(defaultValueSupplier);
            sb.append('}');
            return sb.toString();
        }
    }
}
