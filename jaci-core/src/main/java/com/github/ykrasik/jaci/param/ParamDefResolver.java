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

/**
 * A {@link ParamDef} is just a definition for a set of properties about a parameter, without an actual implementation.
 * This class is used to translate a {@link ParamDef} to its concrete implementation of type {@code T}.
 * It is assumed that all parameters of an implementations have a common super-type: {@code T}.
 *
 * @param <T> Concrete paramDef implementation super-type.
 *
 * @author Yevgeny Krasik
 */
public interface ParamDefResolver<T> {
    /**
     * Translate a {@link StringParamDef} to a concrete implementation of a string parameter.
     *
     * @param def ParamDef to translate.
     * @return An implementation-specific string parameter.
     */
    T stringParam(StringParamDef def);

    /**
     * Translate a {@link BooleanParamDef} to a concrete implementation of a boolean parameter.
     *
     * @param def ParamDef to translate.
     * @return An implementation-specific boolean parameter.
     */
    T booleanParam(BooleanParamDef def);

    /**
     * Translate an {@link IntParamDef} to a concrete implementation of an integer parameter.
     *
     * @param def ParamDef to translate.
     * @return An implementation-specific integer parameter.
     */
    T intParam(IntParamDef def);

    /**
     * Translate an {@link DoubleParamDef} to a concrete implementation of a double parameter.
     *
     * @param def ParamDef to translate.
     * @return An implementation-specific double parameter.
     */
    T doubleParam(DoubleParamDef def);

    /**
     * Translate an {@link EnumParamDef} to a concrete implementation of an enum parameter.
     *
     * @param def ParamDef to translate.
     * @return An implementation-specific enum parameter.
     */
    <E extends Enum<E>> T enumParam(EnumParamDef<E> def);
}
