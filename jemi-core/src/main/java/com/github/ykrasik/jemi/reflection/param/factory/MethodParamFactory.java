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

package com.github.ykrasik.jemi.reflection.param.factory;

import com.github.ykrasik.jemi.param.ParamDef;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.reflection.ReflectionParameter;

/**
 * Creates {@link ParamDef}s out of {@link ReflectionParameter}s.
 * Is not required to support all methods,  may signal that a parameter is not supported
 * by returning an {@code absent} value from {@link #create(Object, ReflectionParameter)}.
 *
 * @param <T> Type of ParamDef this factory can create.
 *
 * @author Yevgeny Krasik
 */
public interface MethodParamFactory<T extends ParamDef<?>> {
    /**
     * Process the parameter and create a {@link ParamDef} out of it, if this factory can accept this parameter.
     *
     * @param instance Instance of a class which contains the method for which this parameter is being created.
     * @param param Parameter to be processed.
     * @return A {@code present} {@link ParamDef} if the parameter is accepted by this factory.
     * @throws Exception If any error occurs.
     */
    Opt<T> create(Object instance, ReflectionParameter param) throws Exception;
}
