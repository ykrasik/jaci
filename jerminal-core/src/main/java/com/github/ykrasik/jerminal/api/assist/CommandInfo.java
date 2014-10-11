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

import java.util.List;
import java.util.Objects;

/**
 * Information about a command.
 *
 * @author Yevgeny Krasik
 */
public class CommandInfo {
    private final String commandName;
    private final List<ParamAndValue> paramAndValues;
    private final int currentParamIndex;

    public CommandInfo(String commandName, List<ParamAndValue> paramAndValues, int currentParamIndex) {
        this.commandName = Objects.requireNonNull(commandName);
        this.paramAndValues = Objects.requireNonNull(paramAndValues);
        this.currentParamIndex = Objects.requireNonNull(currentParamIndex);
    }

    /**
     * @return The name of the command.
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * @return The command's parameters and their optionally parsed values, in the order they appear in the command.<br>
     *         The size of this list is equal exactly to the amount of parameters the command defines.
     */
    public List<ParamAndValue> getParamAndValues() {
        return paramAndValues;
    }

    /**
     * @return The index of the current parameter being parsed. This is an index into the list returned by {@link #getParamAndValues()}.<br>
     *         May be -1 if all parameters have been parsed.
     */
    public int getCurrentParamIndex() {
        return currentParamIndex;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommandInfo{");
        sb.append("commandName='").append(commandName).append('\'');
        sb.append(", paramAndValues=").append(paramAndValues);
        sb.append(", currentParamIndex=").append(currentParamIndex);
        sb.append('}');
        return sb.toString();
    }
}
