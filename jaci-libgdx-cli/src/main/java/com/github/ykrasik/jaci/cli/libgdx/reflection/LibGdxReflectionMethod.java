/******************************************************************************
 * Copyright (C) 2016 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jaci.cli.libgdx.reflection;

import com.badlogic.gdx.utils.reflect.Method;
import com.github.ykrasik.jaci.reflection.ReflectionMethod;
import com.github.ykrasik.jaci.reflection.ReflectionParameter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Reflection information about a method, through the libGdx reflection API.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxReflectionMethod implements ReflectionMethod {
    private static final Annotation[] EMPTY_ANNOTATIONS = { };

    private final Method method;
    private final List<ReflectionParameter> parameters;

    public LibGdxReflectionMethod(Method method) {
        this.method = Objects.requireNonNull(method, "method");
        this.parameters = computeParameters();
    }

    private List<ReflectionParameter> computeParameters() {
        final Class<?>[] parameterTypes = method.getParameterTypes();

        final List<ReflectionParameter> params = new ArrayList<>(parameterTypes.length);
        for (int i = 0; i < parameterTypes.length; i++) {
            final Class<?> parameterType = parameterTypes[i];
            // FIXME: Parameter annotations are unsupported for the libGdx reflection API :(
            params.add(new ReflectionParameter(parameterType, EMPTY_ANNOTATIONS, i));
        }
        return Collections.unmodifiableList(params);
    }

    @Override
    public Object invoke(Object obj, Object... args) throws Exception { return method.invoke(obj, args); }

    @Override
    public void setAccessible(boolean flag) throws SecurityException { method.setAccessible(flag); }

    @Override
    public Class<?> getDeclaringClass() { return method.getDeclaringClass(); }

    @Override
    public String getName() { return method.getName(); }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        final com.badlogic.gdx.utils.reflect.Annotation annotation = method.getDeclaredAnnotation(annotationClass);
        return annotation != null ? annotation.getAnnotation(annotationClass) : null;
    }

    @Override
    public List<ReflectionParameter> getParameters() { return parameters; }

    @Override
    public Class<?> getReturnType() { return method.getReturnType(); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LibGdxReflectionMethod{");
        sb.append("method=").append(method);
        sb.append(", parameters=").append(parameters);
        sb.append('}');
        return sb.toString();
    }
}
