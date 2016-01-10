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

package com.github.ykrasik.jaci.reflection;

import com.github.ykrasik.jaci.util.opt.Opt;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;

/**
 * Reflection information about a method parameter. Used due to lack in Java 7.
 *
 * @author Yevgeny Krasik
 */
public class ReflectionParameter {
    private final Class<?> parameterType;
    private final Annotation[] annotations;
    private final int index;

    public ReflectionParameter(Class<?> parameterType, Annotation[] annotations, int index) {
        this.parameterType = Objects.requireNonNull(parameterType, "parameterType");
        this.annotations = Objects.requireNonNull(annotations, "annotations");
        this.index = index;
    }

    /**
     * @return The parameter's type.
     */
    public Class<?> getParameterType() {
        return parameterType;
    }

    /**
     * @return The parameter's annotations.
     */
    public Annotation[] getAnnotations() {
        return annotations;
    }

    /**
     * @return The parameter's index in the list of parameters, starting from 0.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param annotationClass Class of annotation to find.
     * @param <T> Type of annotation to find.
     * @return Annotation of the requested type, if exists.
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> Opt<T> getAnnotation(Class<T> annotationClass) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationClass) {
                return Opt.of((T) annotation);
            }
        }
        return Opt.absent();
    }

    /**
     * Generate a default name for the parameter, since parameter names aren't available in Java 7's reflection API.
     *
     * @return A default-generated parameter name, of the form: "${type}Param${index}"
     */
    public String getDefaultName() {
        final String type = parameterType.getSimpleName().toLowerCase();
        return type + "Param" + index;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReflectionParameter{");
        sb.append("parameterType=").append(parameterType);
        sb.append(", annotations=").append(Arrays.toString(annotations));
        sb.append(", index=").append(index);
        sb.append('}');
        return sb.toString();
    }
}
