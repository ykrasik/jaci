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

package com.github.ykrasik.jemi.cli.assist;

import com.github.ykrasik.jemi.cli.param.CliParam;
import com.github.ykrasik.jemi.util.opt.Opt;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@RequiredArgsConstructor
public class BoundParams {
    @NonNull private final Map<CliParam, Object> values;
    @NonNull private final Opt<CliParam> nextParam;

    // TODO: JavaDoc
    public Opt<Object> getBoundValue(CliParam param) {
        return Opt.ofNullable(values.get(param));
    }

    // TODO: JavaDoc
    public Opt<CliParam> getNextParam() {
        return nextParam;
    }
}
