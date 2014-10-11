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

package com.github.ykrasik.jerminal.api.assist;

import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.google.common.base.Optional;

import java.util.Objects;

/**
 * A parameter with it's optionally parsed value. If the value is absent, this parameter hasn't been
 * bound to a value from the command line yet.
 *
 * @author Yevgeny Krasik
 */
public class ParamAndValue {
    private final CommandParam param;
    private final Optional<String> value;

    public ParamAndValue(CommandParam param, Optional<String> value) {
        this.param = Objects.requireNonNull(param);
        this.value = Objects.requireNonNull(value);
    }

    /**
     * @return The parameter.
     */
    public CommandParam getParam() {
        return param;
    }

    /**
     * @return The value, if there is one.
     */
    public Optional<String> getValue() {
        return value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ParamAndValue{");
        sb.append("param=").append(param);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
