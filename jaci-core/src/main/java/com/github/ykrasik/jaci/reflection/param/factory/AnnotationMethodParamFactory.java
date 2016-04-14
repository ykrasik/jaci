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

package com.github.ykrasik.jaci.reflection.param.factory;

import com.github.ykrasik.jaci.param.ParamDef;
import com.github.ykrasik.jaci.reflection.ReflectionParameter;
import com.github.ykrasik.jaci.util.opt.Opt;

import java.lang.annotation.Annotation;

/**
 * A {@link MethodParamFactory} that can create {@link ParamDef}s out of {@link ReflectionParameter}s
 * that are of specific types (classes), and have a specific annotation.
 *
 * @author Yevgeny Krasik
 */
public abstract class AnnotationMethodParamFactory<T extends ParamDef<?>, A extends Annotation> extends AbstractMethodParamFactory<T> {
    private final Class<A> annotationClass;

    /**
     * @param annotationClass Type of annotation this factory supports.
     * @param acceptedParameterType First type of parameters this factory can accept.
     * @param moreAcceptedParameterTypes Additional types of parameter this factory can accept.
     */
    protected AnnotationMethodParamFactory(Class<A> annotationClass, Class<?> acceptedParameterType, Class<?>... moreAcceptedParameterTypes) {
        super(acceptedParameterType, moreAcceptedParameterTypes);
        this.annotationClass = annotationClass;
    }

    @Override
    protected T doCreate(Object instance, ReflectionParameter param) throws Exception {
        final Opt<A> annotation = param.getAnnotation(annotationClass);
        final String defaultParamName = param.getDefaultName();
        final Class<?> type = param.getParameterType();
        if (annotation.isPresent()) {
            return createFromAnnotation(instance, defaultParamName, annotation.get(), type);
        } else {
            return createDefault(defaultParamName, type);
        }
    }

    /**
     * Create a {@link ParamDef} out of the annotation.
     *
     * @param instance Instance of a class which contains the method for which this parameter is being created.
     * @param defaultParamName Default-generated parameter name.
     * @param annotation Annotation the parameter is annotated with.
     * @param type Parameter type.
     * @return A {@link ParamDef} created out of the annotation.
     * @throws Exception If any error occurs.
     */
    protected abstract T createFromAnnotation(Object instance, String defaultParamName, A annotation, Class<?> type) throws Exception;

    /**
     * Create a default {@link ParamDef} of the supported type.
     *
     * @param defaultParamName Default-generated parameter name.
     * @param type Parameter type.
     * @return A default {@link ParamDef}.
     * @throws Exception If any error occurs.
     */
    protected abstract T createDefault(String defaultParamName, Class<?> type) throws Exception;
}
