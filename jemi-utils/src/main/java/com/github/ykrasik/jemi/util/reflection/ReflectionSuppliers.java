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

package com.github.ykrasik.jemi.util.reflection;

import com.github.ykrasik.jemi.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Utilities for creating {@link Supplier}s out of methods via reflection.
 *
 * @author Yevgeny Krasik
 */
public final class ReflectionSuppliers {
    private ReflectionSuppliers() { }

    // TODO: JavaDoc
    public static <T> Supplier<T> supplier(@NonNull Object instance, @NonNull String supplierName, @NonNull Class<T> suppliedClass) {
        final Method method = ReflectionUtils.getNoArgsMethod(instance.getClass(), supplierName);
        ReflectionUtils.assertReturnValue(method, suppliedClass);
        return new ReflectionSupplier<>(instance, method);
    }

    // TODO: JavaDoc
    public static <T> Supplier<List<T>> listSupplier(@NonNull Object instance, @NonNull String supplierName, @NonNull Class<T[]> suppliedClass) {
        final Supplier<T[]> supplier = supplier(instance, supplierName, suppliedClass);
        return new ReflectionListSupplier<>(supplier);
    }

    /**
     * A {@link Supplier} that invokes a (possibly private) no-args method through reflection.
     *
     * @author Yevgeny Krasik
     */
    @RequiredArgsConstructor
    private static class ReflectionSupplier<T> implements Supplier<T> {
        private final Object instance;
        private final Method method;

        @Override
        public T get() {
            return ReflectionUtils.invokeNoArgs(instance, method);
        }
    }

    /**
     * A {@link Supplier} that invokes a (possibly private) no-args method that returns an array of {@code T}
     * through reflection, and wraps the returned array in a {@link List}.
     *
     * @author Yevgeny Krasik
     */
    @RequiredArgsConstructor
    private static class ReflectionListSupplier<T> implements Supplier<List<T>> {
        private final Supplier<T[]> supplier;

        @Override
        public List<T> get() {
            return Arrays.asList(supplier.get());
        }
    }
}
