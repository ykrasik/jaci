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

package com.github.ykrasik.jaci.reflection;

import com.github.ykrasik.jaci.util.exception.SneakyException;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * Utilities for dealing with reflection.
 *
 * @author Yevgeny Krasik
 */
public final class ReflectionUtils {
    private static final Class<?>[] NO_ARGS_TYPE = {};
    private static final Object[] NO_ARGS = {};

    private ReflectionUtils() { }

    private static ReflectionAccessor accessor;

    /**
     * Set the current instance of a {@link ReflectionAccessor} to be used for all reflection calls.
     * Must be called for any reflection operations to become possible.
     *
     * @param accessor {@link ReflectionAccessor} to be used.
     */
    public static void setReflectionAccessor(ReflectionAccessor accessor) {
        ReflectionUtils.accessor = Objects.requireNonNull(accessor, "accessor");
    }

    /**
     * Creates an instance of this class through reflection. Class must have a no-args constructor.
     * Any exceptions thrown during the process will be re-thrown as unchecked.
     *
     * @param clazz Class to instantiate.
     * @return An instance of the provided class.
     * @throws RuntimeException If an error occurred while instantiating an object of the class (for example, if the
     *                          class doesn't have a no-args constructor).
     */
    public static Object createInstanceNoArgs(Class<?> clazz) {
        assertReflectionAccessor();
        try {
            return accessor.newInstance(clazz);
        } catch (Exception e) {
            throw SneakyException.sneakyThrow(e);
        }
    }

    /**
     * Get all inner classes and interfaces declared by the given class.
     *
     * @param clazz Class to reflect inner classes from.
     * @return An {@code Array} of inner classes and interfaces declared by the given class.
     * @throws RuntimeException If any error occurs.
     */
    public static Class<?>[] getDeclaredClasses(Class<?> clazz) {
        assertReflectionAccessor();
        try {
            return accessor.getDeclaredClasses(clazz);
        } catch (Exception e) {
            throw SneakyException.sneakyThrow(e);
        }
    }

    /**
     * Returns a constructor of the given class that takes the given parameter types.
     *
     * @param clazz Class to reflect constructor for.
     * @param parameterTypes Parameter types of the constructor.
     * @param <T> Class type.
     * @return A constructor of the given class that takes the given parameter types.
     * @throws RuntimeException If the class doesn't have a constructor with the given parameter types.
     */
    public static <T> ReflectionConstructor<T> getDeclaredConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        assertReflectionAccessor();
        try {
            return accessor.getDeclaredConstructor(clazz, parameterTypes);
        } catch (Exception e) {
            throw SneakyException.sneakyThrow(e);
        }
    }

    /**
     * Returns a method with the provided name that takes no-args.
     *
     * @param clazz Class to search.
     * @param methodName Method name.
     * @return A method with the provided name that takes no-args.
     * @throws RuntimeException If the class doesn't contain a no-args method with the given name.
     */
    public static ReflectionMethod getNoArgsMethod(Class<?> clazz, String methodName) {
        assertReflectionAccessor();
        try {
            // TODO: Support inheritance?
            return accessor.getDeclaredMethod(clazz, methodName, NO_ARGS_TYPE);
        } catch (Exception e) {
            throw SneakyException.sneakyThrow(e);
        }
    }

    /**
     * Returns an array containing {@code ReflectionMethod} objects reflecting all the
     * public methods of the class or interface represented by this {@code
     * Class} object, including those declared by the class or interface and
     * those inherited from superclasses and superinterfaces.
     *
     * @param clazz Class to get methods from
     * @return the array of {@code ReflectionMethod} objects representing the
     *         public methods of this class
     */
    public static ReflectionMethod[] getMethods(Class<?> clazz) {
        assertReflectionAccessor();
        return accessor.getMethods(clazz);
    }

    /**
     * Returns an array of {@code Field} objects reflecting all the fields
     * declared by the class or interface represented by this
     * {@code Class} object. This includes public, protected, default
     * (package) access, and private fields, but excludes inherited fields.
     *
     * @param clazz Class to get fields from
     * @return  the array of {@code Field} objects representing all the
     *          declared fields of this class
     */
    public static ReflectionField[] getDeclaredFields(Class<?> clazz) {
        assertReflectionAccessor();
        return accessor.getDeclaredFields(clazz);
    }

    /**
     * Returns this element's annotation for the specified type if
     * such an annotation is <em>present</em>, else null.
     *
     * @param <T> the type of the annotation to query for and return if present
     * @param clazz Class to get annotation from
     * @param annotationClass the Class object corresponding to the
     *        annotation type
     * @return this element's annotation for the specified annotation type if
     *     present on this element, else null
     * @throws NullPointerException if the given annotation class is null
     * @since 1.5
     */
    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationClass) {
        assertReflectionAccessor();
        return accessor.getAnnotation(clazz, annotationClass);
    }

    /**
     * Invokes the method, using the provided instance as 'this'.
     * Method must be no-args and have a return value of type {@code T}.
     * If the method is private, it will be made accessible outside of it's class.
     *
     * @param instance Instance to use as 'this' for invocation.
     * @param method Method to invoke.
     * @param <T> Return type.
     * @return Result of invoking the no-args method.
     * @throws RuntimeException If an error occurred invoking the method.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeNoArgs(Object instance, ReflectionMethod method) {
        method.setAccessible(true);
        try {
            return (T) method.invoke(instance, NO_ARGS);
        } catch (Exception e) {
            throw SneakyException.sneakyThrow(e);
        }
    }

    /**
     * Assert that the given method returns the expected return type.
     *
     * @param method Method to assert.
     * @param expectedReturnType Expected return type of the method.
     * @throws IllegalArgumentException If the method's return type doesn't match the expected type.
     */
    public static void assertReturnValue(ReflectionMethod method, Class<?> expectedReturnType) {
        final Class<?> returnType = method.getReturnType();
        if (returnType != expectedReturnType) {
            final String message = "Class='"+method.getDeclaringClass()+"', method='"+method.getName()+"': Must return a value of type '"+expectedReturnType+"'!";
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the given method takes no parameters.
     *
     * @param method Method to assert.
     * @throws IllegalArgumentException If the method takes any parameters.
     */
    public static void assertNoParameters(ReflectionMethod method) {
        final List<ReflectionParameter> parameters = method.getParameters();
        if (!parameters.isEmpty()) {
            final String message = "Class='"+method.getDeclaringClass()+"', method='"+method.getName()+"': Must take no parameters!";
            throw new IllegalArgumentException(message);
        }
    }

    private static void assertReflectionAccessor() {
        Objects.requireNonNull(accessor, "ReflectionAccessor hasn't been set!");
    }
}
