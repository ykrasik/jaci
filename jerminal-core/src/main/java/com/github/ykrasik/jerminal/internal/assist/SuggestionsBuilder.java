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

package com.github.ykrasik.jerminal.internal.assist;

import com.github.ykrasik.jerminal.api.assist.Suggestions;
import com.github.ykrasik.jerminal.internal.exception.ShellException;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder for a {@link Suggestions}.
 *
 * @author Yevgeny Krasik
 */
public class SuggestionsBuilder {
    private final List<String> directorySuggestions;
    private final List<String> commandSuggestions;
    private final List<String> paramNameSuggestions;
    private final List<String> paramValueSuggestions;

    public SuggestionsBuilder() {
        this.directorySuggestions = new ArrayList<>();
        this.commandSuggestions = new ArrayList<>();
        this.paramNameSuggestions = new ArrayList<>();
        this.paramValueSuggestions = new ArrayList<>();
    }

    public Suggestions build() {
        return new Suggestions(directorySuggestions, commandSuggestions, paramNameSuggestions, paramValueSuggestions);
    }

    public SuggestionsBuilder addSuggestion(AutoCompleteType type, String suggestion) {
        final List<String> suggestions = getSuggestionsByType(type);
        suggestions.add(suggestion);
        return this;
    }

    private List<String> getSuggestionsByType(AutoCompleteType type) {
        switch (type) {
            case DIRECTORY: return directorySuggestions;
            case COMMAND: return commandSuggestions;
            case COMMAND_PARAM_NAME: // Fallthrough
            case COMMAND_PARAM_FLAG: return paramNameSuggestions;
            case COMMAND_PARAM_VALUE: return paramValueSuggestions;
            default: throw new ShellException("Invalid AutoCompleteType: %s", type);
        }
    }
}
