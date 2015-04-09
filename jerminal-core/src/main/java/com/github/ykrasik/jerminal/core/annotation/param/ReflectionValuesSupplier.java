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

package com.github.ykrasik.jerminal.core.annotation.param;

import com.github.ykrasik.jerminal.util.function.Supplier;
import com.github.ykrasik.jerminal.util.reflection.ReflectionUtils;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link Supplier} that invokes a (possibly private) no-args method that returns a String[] through reflection.
 *
 * @author Yevgeny Krasik
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionValuesSupplier implements Supplier<List<String>> {
    @NonNull private final Object instance;
    @NonNull private final Method supplierMethod;

    @Override
    public List<String> get() {
        final String[] values = ReflectionUtils.invokeNoArgs(instance, supplierMethod);
        return Arrays.asList(values);
    }

    // TODO: JavaDoc
    public static ReflectionValuesSupplier of(@NonNull Object instance, @NonNull String supplierName) {
        final Method method = ReflectionUtils.findNoArgsMethod(instance.getClass(), supplierName);
        final Class<?> returnType = method.getReturnType();
        if (returnType != String[].class) {
            final String message = String.format("Invalid value supplier: '%s'. Must be no-args and return an array of String!", supplierName);
            throw new IllegalArgumentException(message);
        }
        return new ReflectionValuesSupplier(instance, method);
    }
}
