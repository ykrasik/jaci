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

package com.github.ykrasik.jaci.reflection;

import com.github.ykrasik.jaci.util.function.Spplr;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Suppliers that supply through reflection, usually by invoking a method.
 *
 * @author Yevgeny Krasik
 */
public final class ReflectionSuppliers {
    private ReflectionSuppliers() { }

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
    public static <T> Spplr<T> reflectionSupplier(Object instance,
                                                  String methodName,
                                                  Class<T> suppliedClass) {
        final ReflectionMethod method = ReflectionUtils.getNoArgsMethod(instance.getClass(), methodName);
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
    private static class ReflectionSupplier<T> implements Spplr<T> {
        private final Object instance;
        private final ReflectionMethod method;

        private ReflectionSupplier(Object instance, ReflectionMethod method) {
            this.instance = Objects.requireNonNull(instance, "instance");
            this.method = Objects.requireNonNull(method, "method");
        }

        @Override
        public T get() {
            return ReflectionUtils.invokeNoArgs(instance, method);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ReflectionSupplier{");
            sb.append("instance=").append(instance);
            sb.append(", method=").append(method);
            sb.append('}');
            return sb.toString();
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
        return new ArrayListSupplier<>(supplier);
    }

    /**
     * A {@link Spplr} that wraps another {@link Spplr} that returns an array of {@code T}, delegates to that supplier
     * and instead wraps the returned array in a {@link List} of {@code T}.
     *
     * @author Yevgeny Krasik
     */
    private static class ArrayListSupplier<T> implements Spplr<List<T>> {
        private final Spplr<T[]> supplier;

        private ArrayListSupplier(Spplr<T[]> supplier) {
            this.supplier = Objects.requireNonNull(supplier);
        }

        @Override
        public List<T> get() {
            return Arrays.asList(supplier.get());
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ReflectionListSupplier{");
            sb.append("supplier=").append(supplier);
            sb.append('}');
            return sb.toString();
        }
    }
}
