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

import com.google.common.base.Objects;
import com.github.ykrasik.jerminal.internal.exception.ShellException;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds possible suggestions, grouped by their type.
 *
 * @author Yevgeny Krasik
 */
public class Suggestions {
    private final List<String> directorySuggestions;
    private final List<String> commandSuggestions;
    private final List<String> paramNameSuggestions;
    private final List<String> paramValueSuggestions;

    public Suggestions() {
        this.directorySuggestions = new ArrayList<>();
        this.commandSuggestions = new ArrayList<>();
        this.paramNameSuggestions = new ArrayList<>();
        this.paramValueSuggestions = new ArrayList<>();
    }

    public List<String> getDirectorySuggestions() {
        return directorySuggestions;
    }

    public List<String> getCommandSuggestions() {
        return commandSuggestions;
    }

    public List<String> getParamNameSuggestions() {
        return paramNameSuggestions;
    }

    public List<String> getParamValueSuggestions() {
        return paramValueSuggestions;
    }

    public void addSuggestion(AutoCompleteType type, String suggestion) {
        final List<String> suggestions;
        switch (type) {
            case DIRECTORY: suggestions = directorySuggestions; break;
            case COMMAND: suggestions = commandSuggestions; break;
            case COMMAND_PARAM_NAME: // Fallthrough
            case COMMAND_PARAM_FLAG: suggestions = paramNameSuggestions; break;
            case COMMAND_PARAM_VALUE: suggestions = paramValueSuggestions; break;
            default: throw new ShellException("Invalid AutoCompleteType: %s", type);
        }
        suggestions.add(suggestion);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("directorySuggestions", directorySuggestions)
            .add("commandSuggestions", commandSuggestions)
            .add("paramNameSuggestions", paramNameSuggestions)
            .add("paramValueSuggestions", paramValueSuggestions)
            .toString();
    }
}
