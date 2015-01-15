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

package com.github.ykrasik.jerminal.internal.util;

import com.google.common.base.Optional;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;

/**
 * Provides reflection information about a parameter. Used due to lack in Java 7.
 *
 * @author Yevgeny Krasik
 */
public class ReflectionParameter {
    private final Class<?> parameterType;
    private final Annotation[] annotations;
    private final int index;

    public ReflectionParameter(Class<?> parameterType, Annotation[] annotations, int index) {
        this.parameterType = Objects.requireNonNull(parameterType);
        this.annotations = Objects.requireNonNull(annotations);
        this.index = index;
    }

    /**
     * @return Parameter class.
     */
    public Class<?> getParameterType() {
        return parameterType;
    }

    /**
     * @return Parameter annotations, non-null.
     */
    public Annotation[] getAnnotations() {
        return annotations;
    }

    /**
     * @param annotationClass Type of annotation to find.
     * @param <T> Type of annotation to find.
     * @return Annotation of the requested type, if exists.
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationClass) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationClass) {
                return Optional.of((T) annotation);
            }
        }
        return Optional.absent();
    }

    /**
     * @return The parameter's index in the list of parameters, starting from 0.
     */
    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ReflectionParameter that = (ReflectionParameter) o;

        if (index != that.index) {
            return false;
        }
        if (!Arrays.equals(annotations, that.annotations)) {
            return false;
        }
        if (!parameterType.equals(that.parameterType)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = parameterType.hashCode();
        result = 31 * result + Arrays.hashCode(annotations);
        result = 31 * result + index;
        return result;
    }
}
