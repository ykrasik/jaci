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
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;

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
            throw ParseErrors.invalidParamValue(getExternalForm(), rawValue);
        }
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix) throws ParseException {
        // Numbers cannot be auto-completed.
        throw ParseErrors.invalidParamValue(getExternalForm(), prefix);
    }

    protected abstract T parseNumber(String rawValue);
}
