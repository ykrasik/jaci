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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Possible suggestions, grouped by their type.
 *
 * @author Yevgeny Krasik
 */
public class Suggestions {
    private final List<String> directorySuggestions;
    private final List<String> commandSuggestions;
    private final List<String> paramNameSuggestions;
    private final List<String> paramValueSuggestions;

    public Suggestions(List<String> directorySuggestions,
                       List<String> commandSuggestions,
                       List<String> paramNameSuggestions,
                       List<String> paramValueSuggestions) {
        this.directorySuggestions = Objects.requireNonNull(directorySuggestions);
        this.commandSuggestions = Objects.requireNonNull(commandSuggestions);
        this.paramNameSuggestions = Objects.requireNonNull(paramNameSuggestions);
        this.paramValueSuggestions = Objects.requireNonNull(paramValueSuggestions);

        sortIfApplicable(directorySuggestions);
        sortIfApplicable(commandSuggestions);
        sortIfApplicable(paramNameSuggestions);
        sortIfApplicable(paramValueSuggestions);
    }

    private void sortIfApplicable(List<String> suggestions) {
        if (suggestions.size() > 1) {
            Collections.sort(suggestions);
        }
    }

    /**
     * @return Directory suggestions.
     */
    public List<String> getDirectorySuggestions() {
        return directorySuggestions;
    }

    /**
     * @return Command name suggestions.
     */
    public List<String> getCommandSuggestions() {
        return commandSuggestions;
    }

    /**
     * @return Command parameter name suggestions.
     */
    public List<String> getParamNameSuggestions() {
        return paramNameSuggestions;
    }

    /**
     * @return Command parameter value suggestions.
     */
    public List<String> getParamValueSuggestions() {
        return paramValueSuggestions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Suggestions{");
        sb.append("directorySuggestions=").append(directorySuggestions);
        sb.append(", commandSuggestions=").append(commandSuggestions);
        sb.append(", paramNameSuggestions=").append(paramNameSuggestions);
        sb.append(", paramValueSuggestions=").append(paramValueSuggestions);
        sb.append('}');
        return sb.toString();
    }
}
