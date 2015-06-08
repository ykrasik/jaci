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

package com.github.ykrasik.jemi.util.function;

import com.github.ykrasik.jemi.util.reflection.ReflectionUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Supplier related utilities.
 *
 * @author Yevgeny Krasik
 */
public final class MoreSuppliers {
    private MoreSuppliers() { }

    /**
     * Create a supplier that will return the given value.
     *
     * @param value Value to return.
     * @param <T> Value type.
     * @return A {@link Spplr} that will return the given constant value.
     */
    public static <T> Spplr<T> of(T value) {
        return new ConstSupplier<>(value);
    }

    /**
     * A {@link Spplr} that returns a constant value.
     */
    @ToString
    @RequiredArgsConstructor
    private static class ConstSupplier<T> implements Spplr<T> {
        private final T value;

        @Override
        public T get() {
            return value;
        }
    }

    /**
     * Create a supplier that will transform the value returned by the given supplier by invoking the given function on it.
     * This operation is also known as 'map'.
     * If the given supplier is a const supplier type (supplies a constant or cache value), the returned supplier
     * will also cache the result of the transformation and not re-calculate it on every call.
     *
     * @param supplier Supplier to transform.
     * @param function Function to transform the supplier with.
     * @param <T> Supplied value type.
     * @param <R> Transformed value type.
     * @return A {@link Spplr} that will apply the given function to the value supplied by the given supplier.
     */
    public static <T, R> Spplr<R> map(@NonNull Spplr<T> supplier, @NonNull Func<T, R> function) {
        if (supplier instanceof ConstSupplier || supplier instanceof CachingSupplier) {
            return of(function.apply(supplier.get()));
        }
        return new TransformingSupplier<>(supplier, function);
    }

    /**
     * A {@link Spplr} that returns a the result of applying a function to the value supplied by the wrapped supplier.
     */
    @ToString(of = "supplier")
    @RequiredArgsConstructor
    private static class TransformingSupplier<T, R> implements Spplr<R> {
        private final Spplr<T> supplier;
        private final Func<T, R> function;

        @Override
        public R get() {
            return function.apply(supplier.get());
        }
    }

    // TODO: JavaDoc - Returns a supplier that only invokes the supplier once and then returns the cached result.

    /**
     * Wrap the given supplier in a supplier that will only call the underlying supplier once,
     * and then cache the returned value. The following calls will returned the cached value.
     * A 'lazy supplier' of sorts.
     *
     * @param supplier Supplier to cache.
     * @param <T> Supplied value type.
     * @return A {@link Spplr} that will call the wrapped supplier once and only once and cache the value for future
     *         calls.
     */
    public static <T> Spplr<T> cache(@NonNull Spplr<T> supplier) {
        return new CachingSupplier<>(supplier);
    }

    /**
     * A {@link Spplr} that calls the underlying supplier once and only once and caches the returned value for future calls.
     */
    @ToString
    private static class CachingSupplier<T> implements Spplr<T> {
        private Spplr<T> supplier;
        private volatile T value;

        private CachingSupplier(Spplr<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T get() {
            if (value == null) {
                // Double-checked locking.
                synchronized (this) {
                    if (value == null) {
                        value = supplier.get();
                        supplier = null;    // Release reference to gc.
                    }
                }
            }
            return value;
        }
    }

    /**
     * Create a supplier that will invoke the method specified by the give method name
     * on the given object instance through reflection.
     * The method must be no-args and must return a value of the specified type.
     * The method may be private, in which case it will be made accessible outside it's class.
     *
     * @param instance Instance to invoke the method one.
     * @param methodName Method name to invoke. Method must be no-args and return a value of the specified type.
     * @param suppliedClass Expected return type of the method.
     * @param <T> Supplier return type.
     * @return A {@link Spplr} that will invoke the no-args method specified by the given name on the given instance.
     */
    public static <T> Spplr<T> reflectionSupplier(@NonNull Object instance,
                                                  @NonNull String methodName,
                                                  @NonNull Class<T> suppliedClass) {
        final Method method = ReflectionUtils.getNoArgsMethod(instance.getClass(), methodName);
        ReflectionUtils.assertReturnValue(method, suppliedClass);
        return new ReflectionSupplier<>(instance, method);
    }

    /**
     * Similar to {@link #reflectionSupplier(Object, String, Class)}, except allows for an alternative return type.
     * Will first try to create a reflection supplier using the primary class, but if that fails (the method doesn't
     * return the primary class), will re-try with the secondary class.
     * Useful for cases where the return type could be either a primitive or the primitive's boxed version
     * (i.e. Integer.type or Integer.class).
     *
     * @param instance Instance to invoke the method one.
     * @param methodName Method name to invoke. Method must be no-args and return a value of
     *                   either the primary class or the secondary type.
     * @param primaryClass Primary return type of the method. Will be tried first.
     * @param secondaryClass Secondary return type of the method.
     *                       Will be tried if the method doesn't return the primary type.
     * @param <T> Supplier return type.
     * @return A {@link Spplr} that will invoke the no-args method specified by the given name on the given instance.
     */
    public static <T> Spplr<T> reflectionSupplier(Object instance,
                                                  String methodName,
                                                  Class<T> primaryClass,
                                                  Class<T> secondaryClass) {
        try {
            return reflectionSupplier(instance, methodName, primaryClass);
        } catch (IllegalArgumentException e) {
            // Try the alternative return value.
            return reflectionSupplier(instance, methodName, secondaryClass);
        }
    }

    /**
     * A {@link Spplr} that invokes a (possibly private) no-args method through reflection.
     *
     * @author Yevgeny Krasik
     */
    @ToString
    @RequiredArgsConstructor
    private static class ReflectionSupplier<T> implements Spplr<T> {
        private final Object instance;
        private final Method method;

        @Override
        public T get() {
            return ReflectionUtils.invokeNoArgs(instance, method);
        }
    }

    /**
     * Create a supplier that will invoke the method specified by the give method name
     * on the given object instance through reflection.
     * The created supplier will supply a {@link List} of values.
     * The method must be no-args and must return an array of the specified type.
     * The method may be private, in which case it will be made accessible outside it's class.
     *
     * @param instance Instance to invoke the method one.
     * @param methodName Method name to invoke. Method must be no-args and return an array of the specified type.
     * @param suppliedClass Expected array return type of the method.
     * @param <T> Supplier return type.
     * @return A {@link Spplr} that will invoke the no-args method specified by the given name on the given instance.
     */
    public static <T> Spplr<List<T>> reflectionListSupplier(Object instance, String methodName, Class<T[]> suppliedClass) {
        final Spplr<T[]> supplier = reflectionSupplier(instance, methodName, suppliedClass);
        return new ReflectionListSupplier<>(supplier);
    }

    /**
     * A {@link Spplr} that invokes a (possibly private) no-args method that returns an array of {@code T}
     * through reflection, and wraps the returned array in a {@link List}.
     *
     * @author Yevgeny Krasik
     */
    @ToString
    @RequiredArgsConstructor
    private static class ReflectionListSupplier<T> implements Spplr<List<T>> {
        private final Spplr<T[]> supplier;

        @Override
        public List<T> get() {
            return Arrays.asList(supplier.get());
        }
    }
}
