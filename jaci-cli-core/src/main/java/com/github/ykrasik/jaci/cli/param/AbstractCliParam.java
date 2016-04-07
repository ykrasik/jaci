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
import com.github.ykrasik.jaci.cli.exception.ParseError;
import com.github.ykrasik.jaci.cli.exception.ParseException;
import com.github.ykrasik.jaci.util.function.Spplr;
import com.github.ykrasik.jaci.util.opt.Opt;

import java.util.Objects;

/**
 * An abstract implementation of a {@link CliParam}.
 * Handles most common cases like optional values.
 * Specialized for a value type.
 *
 * @param <T> The type of values parsed by this parameter.
 *
 * @author Yevgeny Krasik
 */
public abstract class AbstractCliParam<T> implements CliParam {
    /**
     * This parameter's identifier.
     */
    private final Identifier identifier;

    /**
     * If this value is present, this parameter is considered optional.
     */
    private final Opt<Spplr<T>> defaultValueSupplier;

    protected AbstractCliParam(Identifier identifier, Opt<Spplr<T>> defaultValueSupplier) {
        this.identifier = Objects.requireNonNull(identifier, "identifier");
        this.defaultValueSupplier = Objects.requireNonNull(defaultValueSupplier, "defaultValueSupplier");
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public String toExternalForm() {
        return new StringBuilder()
            .append(isOptional() ? '[' : '{')
            .append(getName())
            .append(" : ")
            .append(getValueTypeName())
            .append(isOptional() ? ']' : '}')
            .toString();
    }

    // Type specialization - subclasses must parse a value of type T.
    @Override
    public abstract T parse(String arg) throws ParseException;

    @Override
    public T noValue() throws ParseException {
        throw missingParamValue();
    }

    @Override
    public T unbound() throws ParseException {
        if (!isOptional()) {
            throw missingParamValue();
        }
        return defaultValueSupplier.get().get();
    }

    private boolean isOptional() {
        // A parameter is considered optional if it has a defaultValueSupplier.
        return defaultValueSupplier.isPresent();
    }

    private ParseException missingParamValue() throws ParseException {
        throw new ParseException(ParseError.PARAM_NOT_BOUND, "Parameter value missing: '"+getName()+'\'');
    }

    protected ParseException invalidParamValue(String value) throws ParseException {
        throw new ParseException(
            ParseError.INVALID_PARAM_VALUE,
            "Invalid value for "+getValueTypeName()+" parameter '"+getName()+"': '"+value+'\''
        );
    }

    /**
     * @return The name of the value type parsed by this parameter.
     */
    protected abstract String getValueTypeName();

    private String getName() {
        return identifier.getName();
    }

    @Override
    public String toString() {
        return toExternalForm();
    }
}
