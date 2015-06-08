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

import com.github.ykrasik.jaci.Identifiable;
import com.github.ykrasik.jaci.util.function.Spplr;
import com.github.ykrasik.jaci.util.opt.Opt;

/**
 * A definition for a parameter.<br>
 * The parameter definition is basically it's interface. It is not a concrete implementation, and any system that wishes
 * to use this definition will need to construct it's own concrete implementation according to this definition.
 * This can be achieved by calling a {@link #resolve(ParamDefResolver)},
 * which will translate the definition to a concrete implementation.
 * <br>
 * Contains the parameter's name, description, whether the parameter is optional and any other parameter-type-specific information.
 * <br/>
 * Any parameter can be optional. A parameter will be considered optional if {@link #getDefaultValueSupplier()} returns
 * a {@code present} value.<br>
 *
 * @author Yevgeny Krasik
 */
public interface ParamDef<T> extends Identifiable {
    /**
     * If this returns a {@code present} value, this paramDef will be considered optional and the returned {@link Spplr}
     * will be invoked when a value isn't explicitly passed to this parameter.
     * Otherwise, the parameter will be considered mandatory and not passing this value will be considered a parse error.
     *
     * @return A {@code present} {@link Spplr} if this parameter should be optional, or an {@code absent} one otherwise.
     */
    Opt<Spplr<T>> getDefaultValueSupplier();

    /**
     * Resolve this paramDef into a concrete implementation using double-dispatch.
     * ParamDefs are implementation-independent, and usually come as a {@link java.util.List}, so the resolver can be
     * used to help get the actual paramDef type and translate it into the implementation-specific type.
     *
     * @param resolver Resolver to resolve this paramDef.
     * @param <E> Concrete parameter implementation type.
     * @return A concrete implementation of the paramDef.
     */
    <E> E resolve(ParamDefResolver<E> resolver);
}
