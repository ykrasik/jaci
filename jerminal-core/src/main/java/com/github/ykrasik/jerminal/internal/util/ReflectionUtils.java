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

package com.github.ykrasik.jerminal.internal.util;

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

    private ReflectionUtils() {

    }

    /**
     * Creates an instance of this class through reflection. Class must have a no-args constructor.
     *
     * @param clazz Class to instantiate.
     * @return An instance of the provided class.
     * @throws java.lang.IllegalArgumentException If an error occurred trying to instantiate the class, like
     *                                            if the class doesn't have a no-args constructor.
     */
    public static Object createInstanceNoArgs(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error instantiating new instance of type: " + clazz, e);
        }
    }

    /**
     * Find a method with the provided name that takes no-args and returns the provided return type.
     * If the method is private, it will be made invokable outside of it's class.
     *
     * @param clazz Class to search.
     * @param methodName Method name.
     * @param returnType The method's expected return type.
     * @return A method with the provided name that takes no-args and returns the provided return type.
     * @throws java.lang.IllegalArgumentException If the class doesn't contain a method with the provided name or
     *                                            if the method doesn't return the expected return type.
     */
    public static Method findNoArgsMethod(Class<?> clazz, String methodName, Class<?> returnType) {
        try {
            final Method method = clazz.getDeclaredMethod(methodName, NO_ARGS_TYPE);
            final Class<?> methodReturnType = method.getReturnType();
            if (methodReturnType != returnType) {
                final String message = String.format("Method does not return expected return type: expected=%s, actual=%s", returnType, methodReturnType);
                throw new IllegalArgumentException(message);
            }
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method;
        } catch (NoSuchMethodException e) {
            final String message = String.format("Method does not exist in class: class=%s, method=%s", clazz, methodName);
            throw new IllegalArgumentException(message, e);
        }
    }

    /**
     * Find the first encountered method with the provided name. Doesn't take parameter into account.
     * Includes inherited methods.
     *
     * @param clazz Class to search.
     * @param methodName Method name.
     * @return First encountered method with the provided name.
     * @throws java.lang.IllegalArgumentException If the class doesn't contain a method with the provided name.
     */
    public static Method lookupMethod(Class<?> clazz, String methodName) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new IllegalArgumentException(String.format("Class '%s' doesn't have method: '%s'", clazz, methodName));
    }

    /**
     * Invokes the method, using the provided instance as 'this', and casts the return value to the provided return type.
     * Method must be no-args and return the correct type.
     *
     * @param instance Instance to use as 'this' for invocation.
     * @param method Method to invoke.
     * @param returnType Return type to cast the method's return value.
     * @param <T> Return type.
     * @return The result of invoking the no-args method, cast to the provided type.
     * @throws java.lang.IllegalStateException If an error occurred invoking the method.
     */
    public static <T> T invokeNoArgs(Object instance, Method method, Class<T> returnType) {
        try {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            final Object returnValue = method.invoke(instance, NO_ARGS);
            return returnType.cast(returnValue);
        } catch (Exception e) {
            final String message = String.format(
                "Error invoking no-args method: class=%s, method=%s",
                method.getDeclaringClass(), method.getName()
            );
            throw new IllegalStateException(message, e);
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
