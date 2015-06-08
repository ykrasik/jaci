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

package com.github.ykrasik.jaci.cli.assist;

import com.github.ykrasik.jaci.cli.param.CliParam;
import com.github.ykrasik.jaci.util.opt.Opt;
import lombok.NonNull;
import lombok.ToString;

import java.util.Map;

/**
 * Contains a possibly-partial state of parsing a command's parameters.
 * Contains all parameters that were bound (and parsed), as well as the next parameter to be parsed.
 *
 * @author Yevgeny Krasik
 */
@ToString
public class BoundParams {
    /**
     * Parsed parameter values.
     */
    private final Map<CliParam, Object> values;

    /**
     * Next parameter to be parsed.
     */
    private final Opt<CliParam> nextParam;

    public BoundParams(@NonNull Map<CliParam, Object> values, @NonNull Opt<CliParam> nextParam) {
        this.values = values;
        this.nextParam = nextParam;
    }

    /**
     * @param param Parameter to get the bound value for.
     * @return A {@code present} value containing the parameter's parsed argument,
     *         if the given parameter has been parsed.
     */
    public Opt<Object> getBoundValue(CliParam param) {
        return Opt.ofNullable(values.get(param));
    }

    /**
     * @return A {@code present} value containing the next parameter to be parsed, if such a parameter exists.
     *         Will be {@code absent} if all command parameters have been parsed.
     */
    public Opt<CliParam> getNextParam() {
        return nextParam;
    }
}
