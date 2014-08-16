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

package com.github.ykrasik.jerminal.api.command.parameter.numeric;

import com.github.ykrasik.jerminal.internal.command.parameter.AbstractMandatoryCommandParam;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteReturnValue;
import com.github.ykrasik.jerminal.internal.exception.ParseError;

/**
 * An abstract implementation of a numeric {@link com.github.ykrasik.jerminal.api.command.parameter.CommandParam CommandParam}.
 *
 * @author Yevgeny Krasik
 */
public abstract class AbstractNumericCommandParam<T> extends AbstractMandatoryCommandParam<T> {
    protected AbstractNumericCommandParam(String name, String description) {
        super(name, description);
    }

    @Override
    public T parse(String rawValue) throws ParseException {
        try {
            return parseNumber(rawValue);
        } catch (NumberFormatException ignored) {
            throw invalidParamValue(rawValue);
        }
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix) throws ParseException {
        // Numbers cannot be auto-completed.
        throw autoCompleteImpossible();
    }

    protected abstract T parseNumber(String rawValue);

    private ParseException invalidParamValue(String value) {
        return new ParseException(
            ParseError.INVALID_PARAM_VALUE,
            "Invalid value for %s parameter '%s': '%s'", getExternalFormType(), getName(), value
        );
    }

    private ParseException autoCompleteImpossible() {
        return new ParseException(
            ParseError.INVALID_PARAM_VALUE,
            "Cannot autoComplete %s parameters '%s'!'", getExternalFormType(), getName()
        );
    }
}
