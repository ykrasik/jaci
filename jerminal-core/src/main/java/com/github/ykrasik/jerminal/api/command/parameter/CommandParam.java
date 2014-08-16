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

package com.github.ykrasik.jerminal.api.command.parameter;

import com.github.ykrasik.jerminal.internal.Describable;
import com.github.ykrasik.jerminal.internal.command.parameter.ParamType;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteReturnValue;

// TODO: Rename this to just Parameter.
/**
 * A command parameter.<br>
 * Parameters need to be able to parse values they are given and autoComplete them from prefixes.
 *
 * @author Yevgeny Krasik
 */
public interface CommandParam extends Describable {
    /**
     * Returns the type of the parameter.
     */
    ParamType getType();

    /**
     * Returns the external form representation of the parameter.
     */
    String getExternalForm();

    /**
     * Parse the rawValue into a real value that this parameter can accept.
     * @throws ParseException If the rawValue is not parsable.
     */
    Object parse(String rawValue) throws ParseException;

    /**
     * Return a value that this parameter can accept when it wasn't explicitly bound to one.
     * @throws ParseException If this parameter must be explicitly bound.
     */
    Object unbound() throws ParseException;

    /**
     * Offer assistance for values this parameter can accept that start with the given prefix.
     * @throws ParseException If the given prefix is invalid or the parameter cannot be auto completed.
     */
    AutoCompleteReturnValue autoComplete(String prefix) throws ParseException;
}
