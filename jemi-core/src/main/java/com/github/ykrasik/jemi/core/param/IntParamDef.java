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
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@EqualsAndHashCode(callSuper = true)
public class IntParamDef extends AbstractParamDef<Integer> {
    private IntParamDef(Identifier identifier, Opt<Supplier<Integer>> defaultValueSupplier) {
        super(identifier, defaultValueSupplier);
    }

    @Override
    public <E> E resolve(ParamDefResolver<E> resolver) {
        return resolver.intParam(this);
    }

    // TODO: JavaDoc
    @Accessors(chain = true)
    public static class Builder {
        private final String name;

        @Setter
        @NonNull private String description = "int";

        private Opt<Supplier<Integer>> defaultValueSupplier = Opt.absent();

        public Builder(@NonNull String name) {
            this.name = name;
        }

        public Builder setOptional(int defaultValue) {
            return setOptional(Suppliers.of(defaultValue));
        }

        public Builder setOptional(@NonNull Supplier<Integer> defaultValueSupplier) {
            this.defaultValueSupplier = Opt.of(defaultValueSupplier);
            return this;
        }

        public IntParamDef build() {
            final Identifier identifier = new Identifier(name, description);
            return new IntParamDef(identifier, defaultValueSupplier);
        }
    }
}
