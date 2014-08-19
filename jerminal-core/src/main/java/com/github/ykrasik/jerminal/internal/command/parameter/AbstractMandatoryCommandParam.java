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

package com.github.ykrasik.jerminal.internal.command.parameter;

import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.github.ykrasik.jerminal.internal.AbstractDescribable;
import com.github.ykrasik.jerminal.internal.exception.ParseException;

/**
 * An abstract implementation for a mandatory {@link CommandParam}.
 *
 * @author Yevgeny Krasik
 */
public abstract class AbstractMandatoryCommandParam<T> extends AbstractDescribable implements CommandParam {
    protected AbstractMandatoryCommandParam(String name, String description) {
        super(name, description);
    }

    @Override
    public ParamType getType() {
        return ParamType.MANDATORY;
    }

    @Override
    public T unbound() throws ParseException {
        throw paramNotBound();
    }

    @Override
    public String getExternalForm() {
        final String type = getExternalFormType();
        return String.format("{%s: %s}", getName(), type);
    }

    protected abstract String getExternalFormType();

    private ParseException paramNotBound() {
        return new ParseException(
            ParseError.PARAM_NOT_BOUND,
            "Mandatory parameter was not bound: '%s'", getName()
        );
    }

    @Override
    public String toString() {
        return getExternalForm();
    }
}
