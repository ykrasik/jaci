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

package com.github.ykrasik.jemi.cli.param;

import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jemi.core.Identifiable;
import com.github.ykrasik.jerminal.old.assist.AutoCompleteReturnValue;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public interface CliParam extends Identifiable {
    /**
     * @return The external form representation of the parameter.
     */
    String toExternalForm();

    /**
     * Parse the rawValue into a real value that this parameter can accept.
     *
     * @param rawValue Raw value to parse.
     * @return Parsed value if acceptable.
     * @throws ParseException If the raw value is not acceptable by this parameter.
     */
    Object parse(String rawValue) throws ParseException;

    /**
     * This parameter was bound to an empty value. Usually happens when the parameter is passed by name,
     * without an explicit value afterwards.
     *
     * @return A value that this parameter can accept when it is bound to an empty value.
     * @throws ParseException If this parameter must be explicitly bound.
     */
    Object noValue() throws ParseException;

    /**
     * This parameter has not been bound to any value.
     *
     * @return A value that this parameter can accept when it wasn't explicitly bound to one.
     * @throws ParseException If this parameter must be explicitly bound.
     */
    Object unbound() throws ParseException;

    /**
     * @param prefix Prefix to auto complete.
     * @return Auto complete possibilities for values this parameter can accept that start with the given prefix.
     * @throws ParseException If the given prefix is invalid or the parameter cannot be auto completed.
     */
    AutoCompleteReturnValue autoComplete(String prefix) throws ParseException;
}
