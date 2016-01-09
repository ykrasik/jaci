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

package com.github.ykrasik.jaci.cli.assist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Suggestions for words, grouped by their type.
 * Used in auto-complete operations.
 * Built through the {@link Suggestions.Builder} builder.
 *
 * @author Yevgeny Krasik
 */
public class Suggestions {
    private final List<String> directorySuggestions;
    private final List<String> commandSuggestions;
    private final List<String> paramNameSuggestions;
    private final List<String> paramValueSuggestions;

    private Suggestions(List<String> directorySuggestions,
                        List<String> commandSuggestions,
                        List<String> paramNameSuggestions,
                        List<String> paramValueSuggestions) {
        this.directorySuggestions = Objects.requireNonNull(directorySuggestions, "directorySuggestions");
        this.commandSuggestions = Objects.requireNonNull(commandSuggestions, "commandSuggestions");
        this.paramNameSuggestions = Objects.requireNonNull(paramNameSuggestions, "paramNameSuggestions");
        this.paramValueSuggestions = Objects.requireNonNull(paramValueSuggestions, "paramValueSuggestions");
    }

    /**
     * @return Suggestions for directory names.
     */
    public List<String> getDirectorySuggestions() {
        return directorySuggestions;
    }

    /**
     * @return Suggestions for command names.
     */
    public List<String> getCommandSuggestions() {
        return commandSuggestions;
    }

    /**
     * @return Suggestions for parameter names.
     */
    public List<String> getParamNameSuggestions() {
        return paramNameSuggestions;
    }

    /**
     * @return Suggestions for parameter values.
     */
    public List<String> getParamValueSuggestions() {
        return paramValueSuggestions;
    }

    /**
     * A builder for {@link Suggestions}.
     */
    public static class Builder {
        private final List<String> directorySuggestions = new ArrayList<>();
        private final List<String> commandSuggestions = new ArrayList<>();
        private final List<String> paramNameSuggestions = new ArrayList<>();
        private final List<String> paramValueSuggestions = new ArrayList<>();

        /**
         * Add a suggestion to this builder.
         *
         * @param type Suggestion type to add.
         * @param suggestion Word to suggest.
         * @return {@code this}, for chaining.
         */
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

        /**
         * @return {@link Suggestions} built out of this builder's parameters.
         */
        public Suggestions build() {
            sort(directorySuggestions);
            sort(commandSuggestions);
            sort(paramNameSuggestions);
            sort(paramValueSuggestions);
            return new Suggestions(directorySuggestions, commandSuggestions, paramNameSuggestions, paramValueSuggestions);
        }

        private void sort(List<String> suggestions) {
            if (suggestions.size() > 1) {
                Collections.sort(suggestions);
            }
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Builder{");
            sb.append("directorySuggestions=").append(directorySuggestions);
            sb.append(", commandSuggestions=").append(commandSuggestions);
            sb.append(", paramNameSuggestions=").append(paramNameSuggestions);
            sb.append(", paramValueSuggestions=").append(paramValueSuggestions);
            sb.append('}');
            return sb.toString();
        }
    }
}
