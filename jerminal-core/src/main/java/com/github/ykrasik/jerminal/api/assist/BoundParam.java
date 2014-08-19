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
import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * A parameter with it's optionally parsed value. If the value is absent, this parameter hasn't been
 * bound to a value from the command line yet.
 *
 * @author Yevgeny Krasik
 */
public class BoundParam {
    private final CommandParam param;
    private final Optional<Object> value;

    public BoundParam(CommandParam param, Optional<Object> value) {
        this.param = param;
        this.value = value;
    }

    public CommandParam getParam() {
        return param;
    }

    public Optional<Object> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("param", param)
            .add("value", value)
            .toString();
    }
}
