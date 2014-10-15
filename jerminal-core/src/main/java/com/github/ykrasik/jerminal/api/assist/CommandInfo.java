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
import com.github.ykrasik.jerminal.api.filesystem.command.Command;
import com.google.common.base.Optional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Information about a command.
 *
 * @author Yevgeny Krasik
 */
public class CommandInfo {
    private final Command command;
    private final List<Optional<String>> paramValues;
    private final Optional<CommandParam> currentParam;

    public CommandInfo(Command command, List<Optional<String>> paramValues, Optional<CommandParam> currentParam) {
        this.command = Objects.requireNonNull(command);
        this.paramValues = Collections.unmodifiableList(Objects.requireNonNull(paramValues));
        this.currentParam = Objects.requireNonNull(currentParam);
    }

    /**
     * @return The {@link Command}.
     */
    public Command getCommand() {
        return command;
    }

    // FIXME: Incorrect JavaDoc
    /**
     * @return A list of values bound to the command's {@link CommandParam}s, if any.<br>
     *         The values appear in the same order as the {@link CommandParam}s returned by {@link Command#getParams()}.
     */
    public List<Optional<String>> getParamValues() {
        return paramValues;
    }

    /**
     * @return The current {@link CommandParam} being parsed. Will be absent if not parsing a command, or if all the params have been parsed.<br>
     *         If present, will be == to one of the {@link CommandParam}s returned by {@link Command#getParams()}.
     */
    public Optional<CommandParam> getCurrentParam() {
        return currentParam;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommandInfo{");
        sb.append("command=").append(command);
        sb.append(", paramValues=").append(paramValues);
        sb.append(", currentParam=").append(currentParam);
        sb.append('}');
        return sb.toString();
    }
}
