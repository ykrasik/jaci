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
import com.github.ykrasik.jemi.util.function.Supplier;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCliParam<T> implements CliParam {
    @NonNull private final Identifier identifier;

    @NonNull private final Opt<Supplier<T>> defaultValueSupplier;

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
            .append(getParamTypeName())
            .append(isOptional() ? ']' : '}')
            .toString();
    }

    // Type specialization.
    @Override
    public abstract T parse(String rawValue) throws ParseException;

    @Override
    public Object noValue() throws ParseException {
        throw missingParamValue();
    }

    @Override
    public T unbound() throws ParseException {
        if (!defaultValueSupplier.isPresent()) {
            // If the paramDef doesn't have a defaultValueSupplier, it is not optional.
            throw missingParamValue();
        }
        return defaultValueSupplier.get().get();
    }

    private boolean isOptional() {
        return defaultValueSupplier.isPresent();
    }

    private ParseException missingParamValue() throws ParseException {
        throw new ParseException(ParseError.PARAM_NOT_BOUND, "Parameter value missing: '%s'", getName());
    }

    protected ParseException invalidParamValue(String value) throws ParseException {
        throw new ParseException(
            ParseError.INVALID_PARAM_VALUE,
            "Invalid value for %s parameter '%s': '%s'", getParamTypeName(), getName(), value
        );
    }

    protected abstract String getParamTypeName();

    private String getName() {
        return identifier.getName();
    }

    @Override
    public String toString() {
        return toExternalForm();
    }
}
