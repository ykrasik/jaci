package com.rawcod.jerminal.returnvalue.suggestion;

import com.google.common.base.Objects;
import com.rawcod.jerminal.exception.ShellException;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteType;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ykrasik
 * Date: 11/08/2014
 * Time: 20:41
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
            case COMMAND_PARAM_NAME: suggestions = paramNameSuggestions; break;
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
