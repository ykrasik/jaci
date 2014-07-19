package com.rawcod.jerminal.filesystem.entry;

import com.google.common.base.Objects;

import java.util.Collections;
import java.util.List;

/**
 * User: ykrasik
 * Date: 07/01/14
 */
public class ShellSuggestion {
    private static final ShellSuggestion NONE = new ShellSuggestion(null, Collections.<String>emptyList());

    private final String longestPrefix;
    private final List<String> possibilities;

    private ShellSuggestion(String longestPrefix, List<String> possibilities) {
        this.longestPrefix = longestPrefix;
        this.possibilities = possibilities;
    }

    public String getLongestPrefix() {
        return longestPrefix;
    }

    public List<String> getPossibilities() {
        return possibilities;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("longestPrefix", longestPrefix)
            .add("possibilities", possibilities)
            .toString();
    }

    public static ShellSuggestion none() {
        return NONE;
    }

    public static ShellSuggestion single(String possibility) {
        return new ShellSuggestion(possibility, Collections.singletonList(possibility));
    }

    public static ShellSuggestion multiple(String longestPrefix, List<String> possibilities) {
        return new ShellSuggestion(longestPrefix, possibilities);
    }
}
