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

package com.github.ykrasik.jerminal.internal.returnvalue;

import com.github.ykrasik.jerminal.api.command.ShellCommand;
import com.github.ykrasik.jerminal.api.command.parameter.CommandParam;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

import java.util.Map;

/**
 * The return value of an assist operation.<br>
 * Contains auto complete suggestions, as well as info about the command that is being assisted.
 *
 * @author Yevgeny Krasik
 */
public class AssistReturnValue {
    private final Optional<ShellCommand> command;
    private final Optional<CommandParam> currentParam;
    private final Map<String, Object> boundParams;
    private final AutoCompleteReturnValue autoCompleteReturnValue;

    public AssistReturnValue(Optional<ShellCommand> command,
                             Optional<CommandParam> currentParam,
                             Map<String, Object> boundParams,
                             AutoCompleteReturnValue autoCompleteReturnValue) {
        this.command = command;
        this.currentParam = currentParam;
        this.boundParams = boundParams;
        this.autoCompleteReturnValue = autoCompleteReturnValue;
    }

    public Optional<ShellCommand> getCommand() {
        return command;
    }

    public Optional<CommandParam> getCurrentParam() {
        return currentParam;
    }

    public Map<String, Object> getBoundParams() {
        return boundParams;
    }

    public AutoCompleteReturnValue getAutoCompleteReturnValue() {
        return autoCompleteReturnValue;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("command", command)
            .add("currentParam", currentParam)
            .add("boundParams", boundParams)
            .toString();
    }
}
