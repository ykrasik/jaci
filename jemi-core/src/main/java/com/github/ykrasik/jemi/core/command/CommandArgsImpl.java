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
    @NonNull private final List<Object> args;

    private int index = 0;

    @Override
    public List<Object> getArgs() {
        return Collections.unmodifiableList(args);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T popArg(Class<T> clazz) {
        if (index >= args.size()) {
            throw new IllegalArgumentException("No more arguments!");
        }

        final Object value = args.get(index);
        if (!clazz.isInstance(value)) {
            final String message = String.format("Invalid argument type: expected=%s, actual=%s", clazz, value.getClass());
            throw new IllegalArgumentException(message);
        }

        index++;
        return (T) value;
    }

    @Override
    public String popString() {
        return popArg(String.class);
    }

    @Override
    public int popInt() {
        return popArg(Integer.class);
    }

    @Override
    public double popDouble() {
        return popArg(Double.class);
    }

    @Override
    public boolean popBool() {
        return popArg(Boolean.class);
    }
}
