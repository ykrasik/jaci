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

package com.github.ykrasik.jaci.reflection.param;

import com.github.ykrasik.jaci.param.ParamDef;
import com.github.ykrasik.jaci.reflection.param.factory.*;
import com.github.ykrasik.jaci.util.opt.Opt;
import com.github.ykrasik.jaci.util.reflection.ReflectionParameter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Creates {@link ParamDef}s out of {@link ReflectionParameter}s if they are accepted by one of the {@link MethodParamFactory}s.
 *
 * @author Yevgeny Krasik
 */
public class ReflectionParamProcessor {
    private final List<MethodParamFactory<?>> factories;

    /**
     * Create a processor that will accept both annotated and non-annotated parameters of the types
     * {boolean, int, double} (and their boxed versions) and String.
     */
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
    ReflectionParamProcessor(MethodParamFactory<?>... factories) {
        this.factories = Arrays.asList(factories);
    }

    /**
     * Create a {@link ParamDef}s out of a method parameter with optional annotations, through reflection.
     * Since parameter names aren't always available to be reflected, parameter names can only be set through
     * the annotation. If absent, a name will be generated for the parameter.
     *
     * @param instance The instance that contains the method this parameter is being constructed for.
     * @param param Information about the parameter.
     * @return A {@link ParamDef} constructed from the parameter.
     * @throws IllegalArgumentException If the parameter wasn't accepted by any of the {@link MethodParamFactory}s.
     *                                  This means the parameter is of an incompatible type.
     */
    public ParamDef<?> createParam(Object instance, ReflectionParameter param) {
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(param, "param");
        try {
            for (MethodParamFactory<?> factory : factories) {
                final Opt<? extends ParamDef<?>> def = factory.create(instance, param);
                if (def.isPresent()) {
                    return def.get();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // No factory accepted this param.
        throw new IllegalArgumentException("Invalid param: class="+instance.getClass()+", param=" + param);
    }
}
