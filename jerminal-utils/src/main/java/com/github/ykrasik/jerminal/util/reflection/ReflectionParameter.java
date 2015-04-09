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

package com.github.ykrasik.jerminal.util.reflection;

import com.github.ykrasik.jerminal.util.opt.Opt;
import lombok.Data;
import lombok.NonNull;

import java.lang.annotation.Annotation;

/**
 * Provides reflection information about a parameter. Used due to lack in Java 7.
 *
 * @author Yevgeny Krasik
 */
@Data
public class ReflectionParameter {
    @NonNull private final Class<?> parameterType;
    @NonNull private final Annotation[] annotations;

    /**
     * The parameter's index in the list of parameters, starting from 0.
     */
    private final int index;

    /**
     * @param annotationClass Type of annotation to find.
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
}
