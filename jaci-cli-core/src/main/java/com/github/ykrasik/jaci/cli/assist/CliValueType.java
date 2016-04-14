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

package com.github.ykrasik.jaci.cli.assist;

import com.github.ykrasik.jaci.util.function.Func;

import java.util.Objects;

/**
 * The different value types that exist in a CLI.
 *
 * @author Yevgeny Krasik
 */
public enum CliValueType {
    // FIXME: Doesn't belong here.
    // TODO: Have a special type for system commands, so they stand out a bit more?
    DIRECTORY('/'),
    COMMAND(' '),
    COMMAND_PARAM_NAME(' '),
    COMMAND_PARAM_VALUE(' ');

    private final Mapper<?> mapper = new Mapper<>(this);

    private final char suffix;

    CliValueType(char suffix) {
        this.suffix = suffix;
    }

    /**
     * @return The suffix that is expected to appear after each type.
     */
    public char getSuffix() {
        return suffix;
    }

    /**
     * @param <T> Input parameter type.
     * @return A {@link Func} that will always return this {@link CliValueType} for any input parameter.
     */
    @SuppressWarnings("unchecked")
    public <T> Mapper<T> getMapper() {
        return (Mapper<T>) mapper;
    }

    /**
     * A {@link Func} that returns a constant {@link CliValueType} for each input.
     *
     * @param <T> Source type of the function.
     */
    public static class Mapper<T> implements Func<T, CliValueType> {
        private final CliValueType type;

        private Mapper(CliValueType type) {
            this.type = Objects.requireNonNull(type, "type");
        }

        @Override
        public CliValueType apply(T t) {
            return type;
        }
    }
}
