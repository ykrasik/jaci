package com.rawcod.jerminal.filesystem.entry;

import com.google.common.base.Objects;

import java.util.Collections;
import java.util.List;

/**
 * User: ykrasik
 * Date: 07/01/14
 */
public class ShellAutoComplete {
    private static final ShellAutoComplete NO_SUGGESTIONS = new ShellAutoComplete(null, Collections.<String>emptyList());

    private final String longestPrefix;
    private final List<String> suggestions;

    private ShellAutoComplete(String longestPrefix, List<String> suggestions) {
        this.longestPrefix = longestPrefix;
        this.suggestions = suggestions;
    }

    public String getLongestPrefix() {
        return longestPrefix;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public static ShellAutoComplete none() {
        return NO_SUGGESTIONS;
    }

    public static ShellAutoComplete single(String matchedArg) {
        return new ShellAutoComplete(matchedArg, Collections.singletonList(matchedArg));
    }

    public static ShellAutoComplete multiple(String longestPrefix, List<String> suggestions) {
        return new ShellAutoComplete(longestPrefix, suggestions);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("longestPrefix", longestPrefix)
            .add("suggestions", suggestions)
            .toString();
    }
}