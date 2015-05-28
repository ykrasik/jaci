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

package com.github.ykrasik.jemi.cli.arg;

import com.github.ykrasik.jemi.cli.command.CliCommandArgs;
import com.github.ykrasik.jemi.cli.command.CliCommandArgsImpl;
import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jemi.cli.param.CliParam;
import com.github.ykrasik.jemi.cli.param.CliParamsManager;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class ParamParser {
    private final CliParamsManager params;
    private final ParamBinder binder;

    private Opt<CliParam> currentParam = Opt.absent();

    public ParamParser(CliParamsManager params) {
        this(params, new ParamBinder(params));
    }

    /**
     * Package-protected for testing.
     */
    ParamParser(@NonNull CliParamsManager params, @NonNull ParamBinder binder) {
        this.params = params;
        this.binder = binder;
    }

    // TODO: JavaDoc
    public CliCommandArgs parse(List<String> rawValues) throws ParseException {
        // First, bind all values to params.
        final BoundParams boundParams = binder.bindParams(rawValues);

        // Second, parse all bound values.
        final List<Object> positionalArgs = new ArrayList<>(params.size());
        final Map<String, Object> namedArgs = new HashMap<>(params.size());
        for (CliParam param : params) {
            final Object value = parseValue(param, boundParams);
            positionalArgs.add(value);
            namedArgs.put(param.getIdentifier().getName(), value);
        }
        currentParam = Opt.absent();
        return new CliCommandArgsImpl(positionalArgs, namedArgs);
    }

    private Object parseValue(CliParam param, BoundParams boundParams) throws ParseException {
        // Set the current param being parsed.
        currentParam = Opt.of(param);

        // Check if the param was bound to a value.
        final Opt<String> boundValue = boundParams.getBoundValue(param);
        if (boundValue.isPresent()) {
            // Param was bound to a value, have the param parse that value.
            return param.parse(boundValue.get());
        } else {
            // Param wasn't bound to a value, notify the param.
            return param.unbound();
        }
    }
}
