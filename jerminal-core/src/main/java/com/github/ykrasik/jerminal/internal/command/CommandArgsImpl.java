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

package com.github.ykrasik.jerminal.internal.command;

import com.github.ykrasik.jerminal.internal.filesystem.command.InternalCommand;
import com.github.ykrasik.jerminal.internal.filesystem.directory.InternalShellDirectory;

import java.util.*;

/**
 * An implementation for {@link PrivilegedCommandArgs}.
 *
 * @author Yevgeny Krasik
 */
public class CommandArgsImpl implements PrivilegedCommandArgs {
    private final Map<String, Object> namedArgs;
    private final Queue<Object> positionalArgs;

    public CommandArgsImpl(Map<String, Object> namedArgs, Queue<Object> positionalArgs) {
        this.namedArgs = Objects.requireNonNull(namedArgs);
        this.positionalArgs = Objects.requireNonNull(positionalArgs);
    }

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
    public InternalShellDirectory getDirectory(String name) {
        return getArg(name, InternalShellDirectory.class);
    }

    @Override
    public InternalShellDirectory popDirectory() {
        return popArg(InternalShellDirectory.class);
    }

    @Override
    public InternalCommand getCommand(String name) {
        return getArg(name, InternalCommand.class);
    }

    @Override
    public InternalCommand popCommand() {
        return popArg(InternalCommand.class);
    }

    @Override
    public List<Object> getArgValues() {
        return new LinkedList<>(positionalArgs);
    }

    private <T> T getArg(String name, Class<T> clazz) throws IllegalArgumentException {
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

    private <T> T popArg(Class<T> clazz) throws IllegalArgumentException {
        if (positionalArgs.isEmpty()) {
            throw new IllegalArgumentException("No more arguments!");
        }
        final Object value = positionalArgs.poll();
        if (value.getClass() != clazz) {
            final String message = String.format("Value for next positional param is of invalid type: expected=%s, actual=%s", clazz, value.getClass());
            throw new IllegalArgumentException(message);
        }
        return clazz.cast(value);
    }
}
