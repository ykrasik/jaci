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
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public final class Suppliers {
    private Suppliers() { }

    // TODO: JavaDoc
    public static <T> Supplier<T> of(T value) {
        return new ConstSupplier<>(value);
    }

    @ToString
    @RequiredArgsConstructor
    private static class ConstSupplier<T> implements Supplier<T> {
        private final T value;

        @Override
        public T get() {
            return value;
        }
    }

    // TODO: JavaDoc - Transforms the value returned by the supplier by invoking the function.
    public static <T, R> Supplier<R> transform(@NonNull Supplier<T> supplier, @NonNull Function<T, R> function) {
        // Not the cleanest solution...
        if (supplier instanceof ConstSupplier || supplier instanceof CachingSupplier) {
            return of(function.apply(supplier.get()));
        }
        return new TransformingSupplier<>(supplier, function);
    }

    @ToString(of = "supplier")
    @RequiredArgsConstructor
    private static class TransformingSupplier<T, R> implements Supplier<R> {
        private final Supplier<T> supplier;
        private final Function<T, R> function;

        @Override
        public R get() {
            return function.apply(supplier.get());
        }
    }

    // TODO: JavaDoc - Returns a supplier that only invokes the supplier once and then returns the cached result.
    public static <T> Supplier<T> cache(@NonNull Supplier<T> supplier) {
        return new CachingSupplier<>(supplier);
    }

    @ToString
    private static class CachingSupplier<T> implements Supplier<T> {
        private Supplier<T> supplier;
        private volatile T value;

        private CachingSupplier(Supplier<T> supplier) {
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

    // TODO: JavaDoc
    public static <T> Supplier<T> reflectionSupplier(@NonNull Object instance,
                                                     @NonNull String methodName,
                                                     @NonNull Class<T> suppliedClass) {
        final Method method = ReflectionUtils.getNoArgsMethod(instance.getClass(), methodName);
        ReflectionUtils.assertReturnValue(method, suppliedClass);
        return new ReflectionSupplier<>(instance, method);
    }

    /**
     * A {@link Supplier} that invokes a (possibly private) no-args method through reflection.
     *
     * @author Yevgeny Krasik
     */
    @ToString
    @RequiredArgsConstructor
    private static class ReflectionSupplier<T> implements Supplier<T> {
        private final Object instance;
        private final Method method;

        @Override
        public T get() {
            return ReflectionUtils.invokeNoArgs(instance, method);
        }
    }

    // TODO: JavaDoc
    public static <T> Supplier<List<T>> reflectionListSupplier(Object instance,
                                                               String methodName,
                                                               Class<T[]> suppliedClass) {
        final Supplier<T[]> supplier = reflectionSupplier(instance, methodName, suppliedClass);
        return new ReflectionListSupplier<>(supplier);
    }

    /**
     * A {@link Supplier} that invokes a (possibly private) no-args method that returns an array of {@code T}
     * through reflection, and wraps the returned array in a {@link List}.
     *
     * @author Yevgeny Krasik
     */
    @ToString
    @RequiredArgsConstructor
    private static class ReflectionListSupplier<T> implements Supplier<List<T>> {
        private final Supplier<T[]> supplier;

        @Override
        public List<T> get() {
            return Arrays.asList(supplier.get());
        }
    }
}
