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

import com.google.common.base.Objects;

import java.util.List;

/**
 * Information about the command on the command line.
 *
 * @author Yevgeny Krasik
 */
public class CommandInfo {
    private final String commandName;
    private final List<ParamAndValue> paramAndValues;
    private final int currentParamIndex;

    public CommandInfo(String commandName, List<ParamAndValue> paramAndValues, int currentParamIndex) {
        this.commandName = commandName;
        this.paramAndValues = paramAndValues;
        this.currentParamIndex = currentParamIndex;
    }

    /**
     * Returns the name of the command specified by the command line.<br>
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Returns all the command's parameters and their optionally parsed values,
     * in the order they appear in the command.<br>
     * The size of this list is equal exactly to the amount of parameters the command defines.
     */
    public List<ParamAndValue> getParamAndValues() {
        return paramAndValues;
    }

    /**
     * Returns the index of the current parameter being parsed.<br>
     * This is an index into the list returned by {@link #getParamAndValues()}.<br>
     * May be -1 if
     */
    public int getCurrentParamIndex() {
        return currentParamIndex;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("commandName", commandName)
            .add("paramAndValues", paramAndValues)
            .add("currentParamIndex", currentParamIndex)
            .toString();
    }
}
