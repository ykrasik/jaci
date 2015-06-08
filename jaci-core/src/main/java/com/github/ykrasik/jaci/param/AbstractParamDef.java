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
import lombok.*;

/**
 * An abstract implementation of a {@link ParamDef}
 *
 * @author Yevgeny Krasik
 */
@EqualsAndHashCode
public abstract class AbstractParamDef<T> implements ParamDef<T> {
    private final Identifier identifier;
    private final Opt<Spplr<T>> defaultValueSupplier;

    protected AbstractParamDef(@NonNull Identifier identifier, @NonNull Opt<Spplr<T>> defaultValueSupplier) {
        this.identifier = identifier;
        this.defaultValueSupplier = defaultValueSupplier;
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
    public String toString() {
        return identifier.toString();
    }
}
