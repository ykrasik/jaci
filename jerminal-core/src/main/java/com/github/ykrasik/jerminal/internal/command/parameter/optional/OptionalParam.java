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

package com.github.ykrasik.jerminal.internal.command.parameter.optional;

import com.google.common.base.Supplier;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.github.ykrasik.jerminal.internal.command.parameter.ParamType;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.assist.AutoCompleteReturnValue;

/**
 * A {@link CommandParam} that is optional.<br>
 * Optional parameters don't have to be bound, and if unbound they will use a default value.
 * This implementation delegates to a concrete {@link CommandParam} except for the call to {@link #unbound()},
 * in which case it returns the default value.
 *
 * @author Yevgeny Krasik
 */
public class OptionalParam<T> implements CommandParam {
    private final CommandParam delegate;
    private final Supplier<T> defaultValueSupplier;

    public OptionalParam(CommandParam delegate, Supplier<T> defaultValueSupplier) {
        this.delegate = delegate;
        this.defaultValueSupplier = defaultValueSupplier;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public ParamType getType() {
        return ParamType.OPTIONAL;
    }

    @Override
    public String getExternalForm() {
        // Replace the original param's braces with '[]'.
        // TODO: This is an assumption that the original param has braces...
        final String originalExternalForm = delegate.getExternalForm();
        return '[' + originalExternalForm.substring(1, originalExternalForm.length() - 1) + ']';
    }

    @Override
    public Object parse(String rawValue) throws ParseException {
        return delegate.parse(rawValue);
    }

    @Override
    public Object unbound() throws ParseException {
        return defaultValueSupplier.get();
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix) throws ParseException {
        return delegate.autoComplete(prefix);
    }

    @Override
    public String toString() {
        return getExternalForm();
    }
}
