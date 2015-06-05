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

import com.github.ykrasik.jemi.cli.exception.ParseError;
import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jemi.Identifier;
import com.github.ykrasik.jemi.cli.assist.AutoComplete;
import com.github.ykrasik.jemi.util.function.Supplier;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.NonNull;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public abstract class AbstractNumericCliParam<T> extends AbstractCliParam<T> {
    protected AbstractNumericCliParam(Identifier identifier, Opt<Supplier<T>> defaultValueSupplier) {
        super(identifier, defaultValueSupplier);
    }

    @Override
    public T parse(@NonNull String rawValue) throws ParseException {
        try {
            return parseNumber(rawValue);
        } catch (NumberFormatException ignored) {
            throw invalidParamValue(rawValue);
        }
    }

    protected abstract T parseNumber(String rawValue);

    @Override
    public AutoComplete autoComplete(@NonNull String prefix) throws ParseException {
        throw new ParseException(ParseError.INVALID_PARAM_VALUE, "Cannot autoComplete %s parameter: '%s'!'", getParamTypeName(), getIdentifier().getName());
    }
}
