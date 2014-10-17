/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.internal.annotation;

import com.github.ykrasik.jerminal.internal.exception.ShellException;
import com.google.common.base.Supplier;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A {@link Supplier} that invokes a (possibly private) no-args method that returns a String[] through reflection.
 *
 * @author Yevgeny Krasik
 */
public class ReflectionValuesSupplier implements Supplier<List<String>> {
    private static final String ERROR_MESSAGE = String.format("Value suppliers must be no-args and return an array of %s!", String.class);

    private final Object instance;
    private final Method supplierMethod;

    public ReflectionValuesSupplier(Object instance, String supplierName) {
        this.instance = Objects.requireNonNull(instance);
        this.supplierMethod = getValuesSupplierMethod(instance, Objects.requireNonNull(supplierName));
    }

    private Method getValuesSupplierMethod(Object instance, String valuesSupplierName) {
        try {
            final Method method = instance.getClass().getDeclaredMethod(valuesSupplierName, null);
            final Class<?> returnType = method.getReturnType();
            if (!returnType.isArray() || returnType.getComponentType() != String.class) {
                throw new IllegalArgumentException(ERROR_MESSAGE);
            }
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(ERROR_MESSAGE, e);
        }
    }

    @Override
    public List<String> get() {
        final String[] values = getValues();
        return Arrays.asList(values);
    }

    private String[] getValues() {
        try {
            return (String[]) supplierMethod.invoke(instance, null);
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }
}