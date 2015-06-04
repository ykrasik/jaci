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

package com.github.ykrasik.jemi.core.reflection.param;

import com.github.ykrasik.jemi.core.param.ParamDef;
import com.github.ykrasik.jemi.core.reflection.param.factory.*;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.reflection.ReflectionParameter;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class ReflectionParamProcessor {
    private final List<MethodParamFactory<?>> factories;

    public ReflectionParamProcessor() {
        this(
            new StringAnnotationParamFactory(),
            new BooleanAnnotationParamFactory(),
            new IntAnnotationParamFactory(),
            new DoubleAnnotationParamFactory()
        );
    }

    /**
     * Package-protected for testing.
     */
    ReflectionParamProcessor(@NonNull MethodParamFactory<?>... factories) {
        this.factories = Arrays.asList(factories);
    }

    /**
     * Create a {@link ParamDef}s out of a method parameter with optional annotations, through reflection.
     * Since parameter names aren't always available to be reflected, parameter names can only be set through
     * the annotation. If absent, a name will be generated for the parameter.
     *
     * @param instance The instance that contains the method this parameter is being generated for.
     * @param param Information about the parameter.
     * @return A {@link ParamDef} constructed from the annotation if it was present, or one with sensible defaults if it wasn't.
     * @throws IllegalArgumentException If the parameter is of an incompatible type.
     *                                  Only accepts {boolean, int, double} (and their boxed versions) and String.
     */
    // TODO: Wrong JavaDoc
    public ParamDef<?> createParam(Object instance, ReflectionParameter param) {
        try {
            return doCreateParam(instance, param);
        } catch (Exception e) {
            final String message = String.format("Error creating parameter: object=%s, param=%s", instance, param);
            throw new IllegalArgumentException(message, e);
        }
    }

    private ParamDef<?> doCreateParam(Object instance, ReflectionParameter param) throws Exception {
        for (MethodParamFactory<?> factory : factories) {
            final Opt<? extends ParamDef<?>> def = factory.create(instance, param);
            if (def.isPresent()) {
                return def.get();
            }
        }
        throw new IllegalArgumentException("Invalid param: " + param);
    }
}
