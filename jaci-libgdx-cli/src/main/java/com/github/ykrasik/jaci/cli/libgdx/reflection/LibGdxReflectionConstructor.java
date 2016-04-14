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

package com.github.ykrasik.jaci.cli.libgdx.reflection;

import com.badlogic.gdx.utils.reflect.Constructor;
import com.github.ykrasik.jaci.reflection.ReflectionConstructor;
import com.github.ykrasik.jaci.util.exception.SneakyException;

import java.util.Objects;

/**
 * Reflection information about a constructor, through the libGdx reflection API.
 *
 * @author Yevgeny Krasik
 */
public class LibGdxReflectionConstructor<T> implements ReflectionConstructor<T> {
    private final Constructor constructor;

    public LibGdxReflectionConstructor(Constructor constructor) {
        this.constructor = Objects.requireNonNull(constructor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T newInstance(Object... args) {
        try {
            return (T) constructor.newInstance(args);
        } catch (Exception e) {
            throw SneakyException.sneakyThrow(e);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LibGdxReflectionConstructor{");
        sb.append("constructor=").append(constructor);
        sb.append('}');
        return sb.toString();
    }
}
