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

import com.github.ykrasik.jaci.Identifier;
import com.github.ykrasik.jaci.cli.assist.AutoComplete;
import com.github.ykrasik.jaci.cli.exception.ParseError;
import com.github.ykrasik.jaci.cli.exception.ParseException;
import com.github.ykrasik.jaci.util.function.Spplr;
import com.github.ykrasik.jaci.util.opt.Opt;

import java.util.Objects;

/**
 * An abstract numeric {@link CliParam}.
 * Numeric parameters cannot be auto completed.
 *
 * @author Yevgeny Krasik
 */
public abstract class AbstractNumericCliParam<T extends Number> extends AbstractCliParam<T> {
    protected AbstractNumericCliParam(Identifier identifier, Opt<Spplr<T>> defaultValueSupplier, boolean nullable) {
        super(identifier, defaultValueSupplier, nullable);
    }

    @Override
    public T parseNonNull(String arg) throws ParseException {
        try {
            return parseNumber(Objects.requireNonNull(arg, "arg"));
        } catch (NumberFormatException ignored) {
            throw invalidParamValue(arg);
        }
    }

    /**
     * Parse the given argument as a number. Concrete number type depends on sub-class implementation.
     *
     * @param arg Argument to parse as a number.
     * @return A parsed number, if the argument is a valid number.
     * @throws NumberFormatException If the argument is not a valid number.
     */
    protected abstract T parseNumber(String arg) throws NumberFormatException;

    @Override
    public AutoComplete autoComplete(String prefix) throws ParseException {
        throw new ParseException(ParseError.INVALID_PARAM_VALUE, "Cannot autoComplete " + getValueTypeName() + " parameter: '" + getIdentifier().getName() + "'!");
    }
}
