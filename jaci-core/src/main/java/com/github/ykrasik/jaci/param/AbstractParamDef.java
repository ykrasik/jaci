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
import com.github.ykrasik.jaci.util.function.Spplr;
import com.github.ykrasik.jaci.util.opt.Opt;

import java.util.Objects;

/**
 * An abstract implementation of a {@link ParamDef}
 *
 * @author Yevgeny Krasik
 */
public abstract class AbstractParamDef<T> implements ParamDef<T> {
    private final Identifier identifier;
    private final Opt<Spplr<T>> defaultValueSupplier;
    private final boolean nullable;

    protected AbstractParamDef(Identifier identifier, Opt<Spplr<T>> defaultValueSupplier, boolean nullable) {
        this.identifier = Objects.requireNonNull(identifier, "identifier");
        this.defaultValueSupplier = Objects.requireNonNull(defaultValueSupplier, "defaultValueSupplier");
        this.nullable = nullable;
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public Opt<Spplr<T>> getDefaultValueSupplier() {
        return defaultValueSupplier;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AbstractParamDef<?> that = (AbstractParamDef<?>) o;

        if (nullable != that.nullable) {
            return false;
        }
        if (!identifier.equals(that.identifier)) {
            return false;
        }
        return defaultValueSupplier.equals(that.defaultValueSupplier);

    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + defaultValueSupplier.hashCode();
        result = 31 * result + (nullable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return identifier.toString();
    }
}
