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

package com.github.ykrasik.jerminal.api.command;

import com.github.ykrasik.jerminal.api.exception.MissingParameterException;
import com.github.ykrasik.jerminal.internal.filesystem.command.InternalCommand;
import com.github.ykrasik.jerminal.internal.filesystem.directory.InternalShellDirectory;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;

/**
 * Holds the args that were passed to the command.<br>
 * Provides 2 ways to access the values - either by name or by position.<br>
 * <br>
 * Access by name is via a map-like API. These calls expect the parameter name, as defined by
 * {@link com.github.ykrasik.jerminal.api.command.parameter.CommandParam#getName()} and will throw a
 * {@link com.github.ykrasik.jerminal.api.exception.MissingParameterException} if trying to retrieve a value
 * for a parameter name that doesn't contain a value, or a {@link java.lang.ClassCastException} if there is
 * a value for that parameter name, but it is of a different class.
 * <br>
 * Access by position is via a stack-like API. Values are popped from the stack and the expected class for the
 * next argument must be provided. If the expected class of the popped parameter doesn't match it's actual class,
 * a {@link java.lang.ClassCastException} will be thrown. The values are ordered in the same order as they appear on
 * {@link com.github.ykrasik.jerminal.api.filesystem.command.Command#getParams()}.
 * <br>
 * Boolean parameters and flags are considered the same for this purpose.
 *
 * @author Yevgeny Krasik
 */
// TODO: Annotation based config.
public class CommandArgs {
    private final Map<String, Object> namedArgs;
    private final Queue<Object> positionalArgs;

    public CommandArgs(Map<String, Object> namedArgs, Queue<Object> positionalArgs) {
        this.namedArgs = Objects.requireNonNull(namedArgs);
        this.positionalArgs = Objects.requireNonNull(positionalArgs);
    }

    /**
     * @param name The parameter name.
     * @return The {@link String} value parsed by the parameter specified by 'name'.
     * @throws MissingParameterException If no value was parsed by the parameter specified by 'name'.
     * @throws java.lang.ClassCastException If the value parsed by the parameter isn't a {@link String}.
     */
    public String getString(String name) throws MissingParameterException {
        return getArg(name, String.class);
    }

    /**
     * @return The next positional {@link String} value.
     * @throws MissingParameterException If there are no more positional values.
     * @throws java.lang.ClassCastException If the next positional value isn't a {@link String}.
     */
    public String popString() throws MissingParameterException {
        return popArg(String.class);
    }

    /**
     * @param name The parameter name.
     * @return The int value parsed by the parameter specified by 'name'.
     * @throws MissingParameterException If no value was parsed by the parameter specified by 'name'.
     * @throws java.lang.ClassCastException If the value parsed by the parameter isn't an int.
     */
    public int getInt(String name) throws MissingParameterException {
        return getArg(name, Integer.class);
    }

    /**
     * @return The next positional int value.
     * @throws MissingParameterException If there are no more positional values.
     * @throws java.lang.ClassCastException If the next positional value isn't an int.
     */
    public int popInt() throws MissingParameterException {
        return popArg(Integer.class);
    }

    /**
     * @param name The parameter name.
     * @return The double value parsed by the parameter specified by 'name'.
     * @throws MissingParameterException If no value was parsed by the parameter specified by 'name'.
     * @throws java.lang.ClassCastException If the value parsed by the parameter isn't a double.
     */
    public double getDouble(String name) throws MissingParameterException {
        return getArg(name, Double.class);
    }

    /**
     * @return The next positional double value.
     * @throws MissingParameterException If there are no more positional values.
     * @throws java.lang.ClassCastException If the next positional value isn't a double.
     */
    public double popDouble() throws MissingParameterException {
        return popArg(Double.class);
    }

    /**
     * Can be used to retrieve both boolean param values and flag values.
     *
     * @param name The parameter name.
     * @return The boolean value parsed by the parameter specified by 'name'.
     * @throws MissingParameterException If no value was parsed by the parameter specified by 'name'.
     * @throws java.lang.ClassCastException If the value parsed by the parameter isn't a boolean.
     */
    public boolean getBool(String name) throws MissingParameterException {
        return getArg(name, Boolean.class);
    }

    /**
     * Can be used to retrieve both boolean param values and flag values.
     *
     * @return The next positional boolean value.
     * @throws MissingParameterException If there are no more positional values.
     * @throws java.lang.ClassCastException If the next positional value isn't a boolean.
     */
    public boolean popBool() throws MissingParameterException {
        return popArg(Boolean.class);
    }

    // FIXME: Hide these, only privileged commands should be able to access these classes.
    /**
     * @param name The parameter name.
     * @return The {@link InternalShellDirectory} value parsed by the parameter specified by 'name'.
     * @throws MissingParameterException If no value was parsed by the parameter specified by 'name'.
     * @throws java.lang.ClassCastException If the value parsed by the parameter isn't an {@link InternalShellDirectory}.
     */
    public InternalShellDirectory getDirectory(String name) throws MissingParameterException {
        return getArg(name, InternalShellDirectory.class);
    }

    /**
     * @return The next positional {@link InternalShellDirectory} value.
     * @throws MissingParameterException If there are no more positional values.
     * @throws java.lang.ClassCastException If the next positional value isn't an {@link InternalShellDirectory}.
     */
    public InternalShellDirectory popDirectory() throws MissingParameterException {
        return popArg(InternalShellDirectory.class);
    }

    /**
     * @param name The parameter name.
     * @return The {@link InternalCommand} value parsed by the parameter specified by 'name'.
     * @throws MissingParameterException If no value was parsed by the parameter specified by 'name'.
     * @throws java.lang.ClassCastException If the value parsed by the parameter isn't an {@link InternalCommand}.
     */
    public InternalCommand getCommand(String name) throws MissingParameterException {
        return getArg(name, InternalCommand.class);
    }

    /**
     * @return The next positional {@link InternalCommand} value.
     * @throws MissingParameterException If there are no more positional values.
     * @throws java.lang.ClassCastException If the next positional value isn't a {@link InternalCommand}.
     */
    public InternalCommand popCommand() throws MissingParameterException {
        return popArg(InternalCommand.class);
    }

    private <T> T getArg(String name, Class<T> clazz) throws MissingParameterException {
        final Object value = namedArgs.get(name);
        if (value == null) {
            throw new MissingParameterException("No value defined for param '%s'!", name);
        }
        return clazz.cast(value);
    }

    private <T> T popArg(Class<T> clazz) throws MissingParameterException {
        if (positionalArgs.isEmpty()) {
            throw new MissingParameterException("No more arguments!");
        }
        final Object value = positionalArgs.poll();
        return clazz.cast(value);
    }
}
