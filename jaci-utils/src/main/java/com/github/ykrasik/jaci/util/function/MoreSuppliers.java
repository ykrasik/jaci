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

package com.github.ykrasik.jaci.util.function;

import java.util.Objects;

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
    private static class ConstSupplier<T> implements Spplr<T> {
        private final T value;

        private ConstSupplier(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ConstSupplier{");
            sb.append("value=").append(value);
            sb.append('}');
            return sb.toString();
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
    public static <T, R> Spplr<R> map(Spplr<T> supplier, Func<T, R> function) {
        if (supplier instanceof ConstSupplier || supplier instanceof CachingSupplier) {
            return of(function.apply(supplier.get()));
        }
        return new TransformingSupplier<>(supplier, function);
    }

    /**
     * A {@link Spplr} that returns a the result of applying a function to the value supplied by the wrapped supplier.
     */
    private static class TransformingSupplier<T, R> implements Spplr<R> {
        private final Spplr<T> supplier;
        private final Func<T, R> function;

        private TransformingSupplier(Spplr<T> supplier, Func<T, R> function) {
            this.supplier = Objects.requireNonNull(supplier, "supplier");
            this.function = Objects.requireNonNull(function, "function");
        }

        @Override
        public R get() {
            return function.apply(supplier.get());
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TransformingSupplier{");
            sb.append("supplier=").append(supplier);
            sb.append('}');
            return sb.toString();
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
    public static <T> Spplr<T> cache(Spplr<T> supplier) {
        return new CachingSupplier<>(supplier);
    }

    /**
     * A {@link Spplr} that calls the underlying supplier once and only once and caches the returned value for future calls.
     */
    private static class CachingSupplier<T> implements Spplr<T> {
        private Spplr<T> supplier;
        private volatile T value;

        private CachingSupplier(Spplr<T> supplier) {
            this.supplier = Objects.requireNonNull(supplier, "supplier");
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

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CachingSupplier{");
            sb.append("supplier=").append(supplier);
            sb.append(", value=").append(value);
            sb.append('}');
            return sb.toString();
        }
    }
}
