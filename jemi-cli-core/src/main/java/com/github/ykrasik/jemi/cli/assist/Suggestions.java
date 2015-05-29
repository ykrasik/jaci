/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jemi.cli.assist;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Possible suggestions, grouped by their type.
 *
 * @author Yevgeny Krasik
 */
@Data
public class Suggestions {
    /**
     * Directory suggestions.
     */
    private final List<String> directorySuggestions;

    /**
     * Command name suggestions.
     */
    private final List<String> commandSuggestions;

    /**
     * Command parameter name suggestions.
     */
    private final List<String> paramNameSuggestions;

    /**
     * Command parameter value suggestions.
     */
    private final List<String> paramValueSuggestions;

    public static class Builder {
        private final List<String> directorySuggestions = new ArrayList<>();
        private final List<String> commandSuggestions = new ArrayList<>();
        private final List<String> paramNameSuggestions = new ArrayList<>();
        private final List<String> paramValueSuggestions = new ArrayList<>();

        public Suggestions build() {
            sort(directorySuggestions);
            sort(commandSuggestions);
            sort(paramNameSuggestions);
            sort(paramValueSuggestions);
            return new Suggestions(directorySuggestions, commandSuggestions, paramNameSuggestions, paramValueSuggestions);
        }

        public Builder addSuggestion(CliValueType type, String suggestion) {
            final List<String> suggestions = getSuggestionsByType(type);
            suggestions.add(suggestion);
            return this;
        }

        private List<String> getSuggestionsByType(CliValueType type) {
            switch (type) {
                case DIRECTORY: return directorySuggestions;
                case COMMAND: return commandSuggestions;
                case COMMAND_PARAM_NAME: return paramNameSuggestions;
                case COMMAND_PARAM_VALUE: return paramValueSuggestions;
                default: throw new IllegalArgumentException("Invalid CliValueType: " + type);
            }
        }

        private void sort(List<String> suggestions) {
            if (suggestions.size() > 1) {
                Collections.sort(suggestions);
            }
        }
    }
}
