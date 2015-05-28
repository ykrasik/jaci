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

package com.github.ykrasik.jemi.core.command;

import java.util.List;

/**
 * Container for args that were passed to the command.<br>
 * Provides 2 ways to access the values - either by name or by position.<br>
 * <br>
 * Access by name is via a map-like API. These calls expect the parameter name, as defined by
 * {@link com.github.ykrasik.jerminal.old.parameter.CommandParam#getName()} and will throw a
 * {@link IllegalArgumentException} if trying to retrieve a value for a parameter name that doesn't contain a value or
 * if there is a value for that parameter name but it is of a different class.
 * <br>
 * Access by position is via a stack-like API. Values are popped from the stack and the expected class for the
 * next argument must be provided. If the expected class of the popped parameter doesn't match it's actual class,
 * a {@link IllegalArgumentException} will be thrown. The values are ordered in the same order as they appear on
 * {@link com.github.ykrasik.jerminal.old.command.Command#getParams()}.
 * <br>
 * Boolean parameters and flags are considered the same for this purpose.
 *
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc is wrong
public interface CommandArgs {
    /**
     * @param name The parameter name.
     * @return The {@link String} value parsed by the parameter specified by 'name'.
     * @throws IllegalArgumentException If no value was parsed by the parameter specified by 'name'
     *                                  or if the value parsed by the parameter isn't a {@link String}.
     */
    String getString(String name);

    /**
     * @return The next positional {@link String} value.
     * @throws IllegalArgumentException If there are no more positional values
     *                                  or if the next positional value isn't a {@link String}.
     */
    String popString();

    /**
     * @param name The parameter name.
     * @return The int value parsed by the parameter specified by 'name'.
     * @throws IllegalArgumentException If no value was parsed by the parameter specified by 'name'
     *                                  or if the value parsed by the parameter isn't an int.
     */
    int getInt(String name);

    /**
     * @return The next positional int value.
     * @throws IllegalArgumentException If there are no more positional values
     *                                  or if the next positional value isn't an int.
     */
    int popInt();

    /**
     * @param name The parameter name.
     * @return The double value parsed by the parameter specified by 'name'.
     * @throws IllegalArgumentException If no value was parsed by the parameter specified by 'name'
     *                                  or if the value parsed by the parameter isn't a double.
     */
    double getDouble(String name);

    /**
     * @return The next positional double value.
     * @throws IllegalArgumentException If there are no more positional values
     *                                  or if the next positional value isn't a double.
     */
    double popDouble();

    /**
     * @param name The parameter name.
     * @return The boolean value parsed by the parameter specified by 'name'.
     * @throws IllegalArgumentException If no value was parsed by the parameter specified by 'name' or
     *                                  if the value parsed by the parameter isn't a boolean.
     */
    boolean getBool(String name);

    /**
     * @return The next positional boolean value.
     * @throws IllegalArgumentException If there are no more positional values or
     *                                  if the next positional value isn't a boolean.
     */
    boolean popBool();

    /**
     * @return All parameter values that were parsed.
     */
    List<Object> getAllValues();
}
