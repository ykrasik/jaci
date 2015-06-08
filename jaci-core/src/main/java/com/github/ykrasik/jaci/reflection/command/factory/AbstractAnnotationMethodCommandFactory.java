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

package com.github.ykrasik.jaci.reflection.command.factory;

import com.github.ykrasik.jaci.command.CommandDef;
import com.github.ykrasik.jaci.util.opt.Opt;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * A {@link MethodCommandFactory} that can create {@link CommandDef}s out of {@link Method}s that are annotated with
 * a single annotation.
 *
 * @author Yevgeny Krasik
 */
public abstract class AbstractAnnotationMethodCommandFactory<T extends Annotation> implements MethodCommandFactory {
    private final Class<T> annotationClass;

    /**
     * @param annotationClass Type of annotation supported by this factory.
     */
    protected AbstractAnnotationMethodCommandFactory(@NonNull Class<T> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public Opt<CommandDef> create(@NonNull Object instance, @NonNull Method method) throws Exception {
        final T annotation = method.getAnnotation(annotationClass);
        if (annotation == null) {
            // Method isn't annotated.
            return Opt.absent();
        }

        return Opt.of(doCreate(instance, method, annotation));
    }

    /**
     * Create a {@link CommandDef} out of the {@link Method} and its annotation.
     *
     * @param instance Instance of a class to which this method belongs.
     * @param method Method to create a {@link CommandDef} out of.
     * @param annotation The method's annotation.
     * @return A {@link CommandDef} created out of the {@link Method} and its annotation.
     * @throws Exception If any error occurs.
     */
    protected abstract CommandDef doCreate(Object instance, Method method, T annotation) throws Exception;
}
