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

package com.github.ykrasik.jaci.cli.param;

import com.github.ykrasik.jaci.Identifiable;
import com.github.ykrasik.jaci.cli.assist.AutoComplete;
import com.github.ykrasik.jaci.cli.exception.ParseException;

/**
 * A CLI implementation of a parameter.
 *
 * @author Yevgeny Krasik
 */
public interface CliParam extends Identifiable {
    /**
     * @return The external form representation of the parameter.
     */
    String toExternalForm();

    /**
     * Parse the argument into a value that this parameter can accept.
     *
     * @param arg Argument to parse.
     * @return Parsed value if the argument is valid.
     * @throws ParseException If the argument is not a valid value for this parameter.
     */
    Object parse(String arg) throws ParseException;

    /**
     * Called to inform the parameter that it wasn't bound to any value.
     * It is up to the parameter to decide if this is valid -
     * if the parameter returns a value, this value will be used as the parameter's value.
     * If this is not valid, the parameter is expected to throw a {@link ParseException}.
     * Typically, only optional parameters should return a value here, mandatory parameters should throw a {@link ParseException}.
     *
     * @return A value that is valid for this parameter when it wasn't explicitly bound to one.
     * @throws ParseException If this parameter must be explicitly bound.
     */
    Object unbound() throws ParseException;

    /**
     * This parameter was bound to an empty value. Usually happens when the parameter is passed by name,
     * without an explicit value afterwards.
     * Invalid in most cases, except if the parameter knows how to handle this situation.
     *
     * @return A value that is valid for this parameter when it is bound to an empty value.
     * @throws ParseException If this parameter must be explicitly bound.
     */
    Object noValue() throws ParseException;

    /**
     * @return Whether this parameter is nullable.
     */
    boolean isNullable();

    /**
     * Auto complete the given prefix.
     *
     * @param prefix Prefix to auto complete.
     * @return Auto complete for values this parameter can accept that start with the given prefix.
     * @throws ParseException If the given prefix is invalid or the parameter cannot be auto completed.
     */
    // TODO: Should the implementation throw a ParseException if the AutoComplete is empty?
    AutoComplete autoComplete(String prefix) throws ParseException;
}
