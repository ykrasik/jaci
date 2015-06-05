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
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

/**
 * A {@link MethodParamFactory} that can create {@link ParamDef}s out of {@link ReflectionParameter}s
 * that are of a specific type (class).
 *
 * @author Yevgeny Krasik
 */
public abstract class AbstractMethodParamFactory<T extends ParamDef<?>> implements MethodParamFactory<T> {
    private final List<Class<?>> acceptedParameterTypes;

    /**
     * @param acceptedParameterTypes Types of parameter this factory can accept.
     */
    protected AbstractMethodParamFactory(@NonNull Class<?>... acceptedParameterTypes) {
        if (acceptedParameterTypes.length == 0) {
            throw new IllegalArgumentException("AbstractMethodParamFactory must process at least 1 parameter type!");
        }
        this.acceptedParameterTypes = Arrays.asList(acceptedParameterTypes);
    }

    @Override
    public Opt<T> create(Object instance, ReflectionParameter param) throws Exception {
        final Class<?> parameterType = param.getParameterType();
        if (!acceptedParameterTypes.contains(parameterType)) {
            // This factory doesn't accept parameter type.
            return Opt.absent();
        }

        return Opt.of(doCreate(instance, param));
    }

    /**
     * Create a {@link ParamDef} out of the {@link ReflectionParameter}, which is guaranteed to be of the accepted type.
     *
     * @param instance Instance of a class which contains the method for which this parameter is being created.
     * @param param Parameter to be processed. Guaranteed to be of the accepted type.
     * @return A {@link ParamDef} created out of this {@link ReflectionParameter}.
     * @throws Exception If any error occurs.
     */
    protected abstract T doCreate(Object instance, ReflectionParameter param) throws Exception;
}
