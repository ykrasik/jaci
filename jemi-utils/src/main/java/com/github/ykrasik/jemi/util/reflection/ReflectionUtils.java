/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jemi.util.reflection;

import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for dealing with reflection.
 *
 * @author Yevgeny Krasik
 */
public final class ReflectionUtils {
    private static final Class<?>[] NO_ARGS_TYPE = {};
    private static final Object[] NO_ARGS = {};

    private ReflectionUtils() { }

    /**
     * Creates an instance of this class through reflection. Class must have a no-args constructor.
     *
     * @param clazz Class to instantiate.
     * @return An instance of the provided class.
     */
    @SneakyThrows
    public static Object createInstanceNoArgs(Class<?> clazz) {
        return clazz.newInstance();
    }

    /**
     * Returns a method with the provided name that takes no-args.
     * If the method is private, it will be made accessible outside of it's class.
     *
     * @param clazz Class to search.
     * @param methodName Method name.
     * @return A method with the provided name that takes no-args.
     * @throws RuntimeException If the class doesn't contain a method with the provided name and args.
     */
    @SneakyThrows
    public static Method getNoArgsMethod(Class<?> clazz, String methodName) {
        // TODO: Support inheritance.
        final Method method = clazz.getDeclaredMethod(methodName, NO_ARGS_TYPE);
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        return method;
    }

    /**
     * Find the first encountered method with the provided name. Doesn't take parameter into account.
     * Includes inherited methods.
     *
     * @param clazz Class to search.
     * @param methodName Method name.
     * @return First encountered method with the provided name.
     * @throws IllegalArgumentException If the class doesn't contain a method with the provided name.
     */
    public static Method lookupMethod(Class<?> clazz, String methodName) {
        // TODO: Support inheritance.
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new IllegalArgumentException(String.format("Class '%s' doesn't have method: '%s'", clazz, methodName));
    }

    /**
     * Invokes the method, using the provided instance as 'this'.
     * Method must be no-args and return the correct type.
     *
     * @param instance Instance to use as 'this' for invocation.
     * @param method Method to invoke.
     * @param <T> Return type.
     * @return The result of invoking the no-args method.
     * @throws RuntimeException If an error occurred invoking the method.
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static <T> T invokeNoArgs(Object instance, Method method) {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        return (T) method.invoke(instance, NO_ARGS);
    }

    // TODO: JavaDoc
    public static void assertReturnValue(Method method, Class<?> expectedReturnType) {
        final Class<?> returnType = method.getReturnType();
        if (returnType != expectedReturnType) {
            final String message = String.format("Class='%s', method='%s': Must return a value of type '%s'!", method.getDeclaringClass(), method.getName(), expectedReturnType);
            throw new IllegalArgumentException(message);
        }
    }

    // TODO: JavaDoc
    public static void assertNoParameters(Method method) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 0) {
            final String message = String.format("Class='%s', method='%s': Must take no parameters!", method.getDeclaringClass(), method.getName());
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Returns basic information about a method's parameters obtained via reflection.
     *
     * @param method Method to reflect parameters for.
     * @return The method's parameter information obtained via reflection.
     */
    public static List<ReflectionParameter> reflectMethodParameters(Method method) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        final List<ReflectionParameter> params = new ArrayList<>(parameterTypes.length);
        for (int i = 0; i < parameterTypes.length; i++) {
            final Class<?> parameterType = parameterTypes[i];
            final Annotation[] annotations = parameterAnnotations[i];
            params.add(new ReflectionParameter(parameterType, annotations, i));
        }
        return params;
    }
}
