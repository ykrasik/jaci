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

import com.github.ykrasik.jerminal.api.assist.CommandInfo;
import com.google.common.base.Optional;

import java.util.Objects;

/**
 * The return value of an assist operation.<br>
 * Contains info about the command as well as auto complete suggestions.
 *
 * @author Yevgeny Krasik
 */
public class AssistReturnValue {
    private final Optional<CommandInfo> commandInfo;
    private final AutoCompleteReturnValue autoCompleteReturnValue;

    public AssistReturnValue(Optional<CommandInfo> commandInfo, AutoCompleteReturnValue autoCompleteReturnValue) {
        this.commandInfo = Objects.requireNonNull(commandInfo);
        this.autoCompleteReturnValue = Objects.requireNonNull(autoCompleteReturnValue);
    }

    public Optional<CommandInfo> getCommandInfo() {
        return commandInfo;
    }

    public AutoCompleteReturnValue getAutoCompleteReturnValue() {
        return autoCompleteReturnValue;
    }

    @Override
    public String toString() {
        return "AssistReturnValue{" + "commandInfo=" + commandInfo + ", autoCompleteReturnValue=" + autoCompleteReturnValue + '}';
    }
}
