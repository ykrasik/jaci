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

package com.github.ykrasik.jemi.core.reflection.param.factory;

import com.github.ykrasik.jemi.core.param.ParamDef;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.reflection.ReflectionParameter;

import java.lang.annotation.Annotation;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public abstract class AnnotationMethodParamFactory<T extends ParamDef<?>, A extends Annotation> extends AbstractMethodParamFactory<T> {
    private final Class<A> annotationClass;

    protected AnnotationMethodParamFactory(Class<A> annotationClass, Class<?>... acceptedParameterTypes) {
        super(acceptedParameterTypes);
        this.annotationClass = annotationClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T doCreate(Object instance, ReflectionParameter param) throws Exception {
        final Opt<A> annotation = param.getAnnotation(annotationClass);
        if (annotation.isPresent()) {
            return createWithAnnotation(instance, param, annotation.get());
        } else {
            return createWithoutAnnotation(instance, param);
        }
    }

    protected abstract T createWithAnnotation(Object instance, ReflectionParameter param, A annotation) throws Exception;
    protected abstract T createWithoutAnnotation(Object instance, ReflectionParameter param) throws Exception;
}
