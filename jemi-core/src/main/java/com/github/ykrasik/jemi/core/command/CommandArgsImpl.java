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

package com.github.ykrasik.jemi.core.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * @author Yevgeny Krasik
 */
@RequiredArgsConstructor
public class CommandArgsImpl implements CommandArgs {
    // FIXME: There is actually no need for named access any more.
    @NonNull private final List<Object> positionalArgs;
    @NonNull private final Map<String, Object> namedArgs;

    private int index = 0;

    @Override
    public String getString(String name) {
        return getArg(name, String.class);
    }

    @Override
    public String popString() {
        return popArg(String.class);
    }

    @Override
    public int getInt(String name) {
        return getArg(name, Integer.class);
    }

    @Override
    public int popInt() {
        return popArg(Integer.class);
    }

    @Override
    public double getDouble(String name) {
        return getArg(name, Double.class);
    }

    @Override
    public double popDouble() {
        return popArg(Double.class);
    }

    @Override
    public boolean getBool(String name) {
        return getArg(name, Boolean.class);
    }

    @Override
    public boolean popBool() {
        return popArg(Boolean.class);
    }

    @Override
    public List<Object> getAllValues() {
        return Collections.unmodifiableList(positionalArgs);
    }

    // TODO: Bah, protected API :/
    protected <T> T getArg(String name, Class<T> clazz) throws IllegalArgumentException {
        final Object value = namedArgs.get(name);
        if (value == null) {
            final String message = String.format("No value found for param '%s'!", name);
            throw new IllegalArgumentException(message);
        }
        if (value.getClass() != clazz) {
            final String message = String.format("Value for param '%s' is of invalid type: expected=%s, actual=%s", name, clazz, value.getClass());
            throw new IllegalArgumentException(message);
        }
        return clazz.cast(value);
    }

    // TODO: Bah, protected API :/
    protected <T> T popArg(Class<T> clazz) throws IllegalArgumentException {
        if (index >= positionalArgs.size()) {
            throw new IllegalArgumentException("No more arguments!");
        }

        final Object value = positionalArgs.get(index);
        if (value.getClass() != clazz) {
            final String message = String.format("Value for next positional param is of invalid type: expected=%s, actual=%s", clazz, value.getClass());
            throw new IllegalArgumentException(message);
        }

        index++;
        return clazz.cast(value);
    }
}
