/******************************************************************************
 * Copyright (c) 2016 Yevgeny Krasik.                                         *
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

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * Reflection information about a constructor, through the Java reflection API.
 *
 * @author Yevgeny Krasik
 */
public class JavaReflectionConstructor<T> implements ReflectionConstructor<T> {
    private final Constructor<T> constructor;

    public JavaReflectionConstructor(Constructor<T> constructor) {
        this.constructor = Objects.requireNonNull(constructor, "constructor");
    }

    @Override
    public T newInstance(Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw SneakyException.sneakyThrow(e);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JavaReflectionConstructor{");
        sb.append("constructor=").append(constructor);
        sb.append('}');
        return sb.toString();
    }
}
