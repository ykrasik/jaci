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

package com.github.ykrasik.jerminal.api.command.parameter.flag;

import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A builder for a {@link FlagParam}.<br>
 * {@link FlagParam}s are always optional.
 *
 * @author Yevgeny Krasik
 */
public class FlagParamBuilder {
    private final String name;
    private String description = "flag";

    public FlagParamBuilder(String name) {
        this.name = checkNotNull(name, "name");
    }

    public CommandParam build() {
        return new FlagParam(name, description);
    }

    public FlagParamBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
}
