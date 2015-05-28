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

import com.github.ykrasik.jemi.cli.CliConstants;
import com.github.ykrasik.jemi.cli.exception.ParseError;
import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jemi.cli.param.CliParam;
import com.github.ykrasik.jemi.cli.param.CliParamsManager;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc - binds params to raw values
@RequiredArgsConstructor
public class ParamBinder {
    private static final BoundParams EMPTY = new BoundParams(Collections.<CliParam, String>emptyMap());

    // FIXME: This can be a parameter, doesn't need to be a part of the state.
    @NonNull private final CliParamsManager params;

    // TODO: JavaDoc
    public BoundParams bindParams(List<String> rawValues) throws ParseException {
        if (params.isEmpty()) {
            if (rawValues.isEmpty()) {
                return EMPTY;
            } else {
                throw new ParseException(ParseError.NO_MORE_PARAMS, "No parameters expected!");
            }
        }

        final ParamBindContext context = new ParamBindContext();
        for (String rawValue : rawValues) {
            processValue(context, rawValue);
        }
        return context.createBoundParams();
    }

    private void processValue(ParamBindContext context, String rawValue) throws ParseException {
        if (!rawValue.startsWith(CliConstants.NAMED_PARAM_PREFIX)) {
            // Value doesn't start with '-', bind it to the next param.
            context.bindNextParam(rawValue);
            return;
        }

        // Value starts with a '-', what comes after is expected to be a valid name of a parameter.
        // Unless, and this is a corner case, the first character of the name is a number, which means we're
        // passing a negative number as a value.
        // Param names cannot start with a number.
        final String paramName = rawValue.substring(1);
        if (paramName.isEmpty()) {
            throw new ParseException(ParseError.INVALID_PARAM, "No parameter name specified after '%s'!", CliConstants.NAMED_PARAM_PREFIX);
        }

        if (Character.isDigit(paramName.charAt(0))) {
            // This value is a negative number, bind it like any other param.
            context.bindNextParam(rawValue);
            return;
        }

        // rawValue starts with '-' and is the name of a parameter, set the context accordingly.
        context.setNextNamedParam(rawValue);
    }

    private class ParamBindContext {
        private final Map<CliParam, String> boundValues = new HashMap<>(params.size());
        private final Queue<CliParam> unboundParams = params.createParamQueue();

        private Opt<CliParam> nextParam = nextPositionalParam();

        private void bindNextParam(String value) throws ParseException {
            if (!nextParam.isPresent()) {
                throw new ParseException(ParseError.INVALID_PARAM, "No parameter to bind value: '%s'", value);
            }
            final CliParam param = nextParam.get();

            final String prevValue = boundValues.put(param, value);
            if (prevValue != null) {
                throw new ParseException(ParseError.PARAM_ALREADY_BOUND, "Parameter '%s' is already bound a value: '%s'",  param.getIdentifier().getName(), prevValue);
            }

            unboundParams.remove(param);

            nextParam = nextPositionalParam();
        }

        private void setNextNamedParam(String paramName) throws ParseException {
            nextParam = params.get(paramName);
            if (!nextParam.isPresent()) {
                throw new ParseException(ParseError.INVALID_PARAM, "Invalid parameter name: '%s'", paramName);
            }
        }

        private Opt<CliParam> nextPositionalParam() {
            return Opt.ofNullable(unboundParams.peek());
        }

        public BoundParams createBoundParams() {
            return new BoundParams(boundValues);
        }
    }
}
