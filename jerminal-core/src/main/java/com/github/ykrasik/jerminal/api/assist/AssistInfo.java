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
 * Assistance information about the command line.
 *
 * @author Yevgeny Krasik
 */
public class AssistInfo {
    private final String commandName;
    private final List<BoundParam> boundParams;
    private final int currentParamIndex;

    public AssistInfo(String commandName, List<BoundParam> boundParams, int currentParamIndex) {
        this.commandName = commandName;
        this.boundParams = boundParams;
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
    public List<BoundParam> getBoundParams() {
        return boundParams;
    }

    /**
     * Returns the index of the current parameter being parsed.<br>
     * This is an index into the list returned by {@link #getBoundParams()}.<br>
     * May be -1 if
     */
    public int getCurrentParamIndex() {
        return currentParamIndex;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("command", commandName)
            .add("boundParams", boundParams)
            .toString();
    }
}
