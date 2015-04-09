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

package com.github.ykrasik.jerminal.core.param;

import com.github.ykrasik.jerminal.core.Identifier;
import com.github.ykrasik.jerminal.util.function.Supplier;
import com.github.ykrasik.jerminal.util.function.Suppliers;
import com.github.ykrasik.jerminal.util.opt.Opt;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class DoubleParamDef extends AbstractParamDef<Double> {
    public DoubleParamDef(Identifier identifier, Opt<Supplier<Double>> defaultValueSupplier) {
        super(identifier, defaultValueSupplier);
    }

    @Override
    public void resolve(ParamDefResolver resolver) {
        resolver.doubleParam(this);
    }

    // TODO: JavaDoc
    @Accessors(chain = true)
    public static class Builder {
        private final String name;

        @Setter
        @NonNull private String description = "double";

        private Opt<Supplier<Double>> defaultValueSupplier = Opt.absent();

        public Builder(@NonNull String name) {
            this.name = name;
        }

        public Builder setOptional(double defaultValue) {
            return setOptional(Suppliers.of(defaultValue));
        }

        public Builder setOptional(@NonNull Supplier<Double> defaultValueSupplier) {
            this.defaultValueSupplier = Opt.of(defaultValueSupplier);
            return this;
        }

        public DoubleParamDef build() {
            final Identifier identifier = new Identifier(name, description);
            return new DoubleParamDef(identifier, defaultValueSupplier);
        }
    }
}
