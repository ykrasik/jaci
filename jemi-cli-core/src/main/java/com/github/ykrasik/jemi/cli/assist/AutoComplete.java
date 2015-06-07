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

import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.string.StringUtils;
import com.github.ykrasik.jemi.util.trie.Trie;
import lombok.NonNull;
import lombok.ToString;

import java.util.Map.Entry;

/**
 * The return value of an auto complete operation.<br>
 * Contains the prefix that was auto completed and a {@link Trie} of possible words from this prefix,
 * with each word's type as it's value.
 *
 * @author Yevgeny Krasik
 */
// TODO: Find a way to get rid of CliValueType and replace it with a char?
// TODO: Find a way to get rid of prefix?
@ToString
public class AutoComplete {
    /**
     * The prefix that was auto completed.
     */
    private final String prefix;

    /**
     * The auto complete possibilities for the prefix.
     */
    private final Trie<CliValueType> possibilities;

    public AutoComplete(@NonNull String prefix, @NonNull Trie<CliValueType> possibilities) {
        this.prefix = prefix;
        this.possibilities = possibilities;
    }

    /**
     * @return Whether this auto-complete has any possibilities.
     */
    public boolean isEmpty() {
        return possibilities.isEmpty();
    }

    /**
     * Create a new auto-complete by merging this auto-complete with the given auto-complete.
     * The resulting auto-complete will contain possibilities from both this and the given auto-complete.
     *
     * @param other Auto-complete to merge with.
     * @return A merged auto-complete containing possibilities from both this and the given auto-complete.
     */
    public AutoComplete union(AutoComplete other) {
        if (possibilities.isEmpty()) {
            return other;
        }
        if (other.possibilities.isEmpty()) {
            return this;
        }

        // TODO: Make sure this can't happen.
        if (!this.prefix.equals(other.prefix)) {
            throw new IllegalArgumentException(String.format("Trying to perform union on different prefixes: prefix1='%s', prefix2='%s'", this.prefix, other.prefix));
        }

        final Trie<CliValueType> unifiedPossibilities = this.possibilities.union(other.possibilities);
        return new AutoComplete(prefix, unifiedPossibilities);
    }

    /**
     * Get a string that can be appended to the prefix from which this auto-complete was built.
     * This is, in essence, the actual auto-complete operation.
     * Will only return a {@code present} value if there is a non-empty string to append.
     *
     * @return A value containing the auto-complete suffix that should be appended to the prefix
     *         from which this auto-complete was constructed, if auto-completion is possible.
     */
    // TODO: Return a simple String, and have the CliShell convert empty strings to empty values?
    public Opt<String> getAutoCompleteSuffix() {
        if (possibilities.isEmpty()) {
            // There are no auto-complete possibilities.
            return Opt.absent();
        }

        if (possibilities.size() > 1) {
            // Multiple auto complete results are possible.
            // AutoComplete as much as is possible - until the longest common prefix.
            final String longestPrefix = possibilities.getLongestPrefix();
            return StringUtils.getNonEmptyString(calcAutoCompleteSuffix(longestPrefix));
        }

        // TODO: Only 1 possibility, boundParams should be updated to show it...
        // Only a single auto complete result is possible, append it to the command line.
        // Let's be helpful - depending on the autoCompleteType,
        // add the suffix that each valueType must have.
        final Entry<String, CliValueType> entry = possibilities.entrySet().iterator().next();
        final String singlePossibility = entry.getKey();
        final CliValueType type = entry.getValue();
        final String suffix = calcAutoCompleteSuffix(singlePossibility);
        return Opt.of(suffix + type.getSuffix());
    }

    private String calcAutoCompleteSuffix(String autoCompletedPrefix) {
        return autoCompletedPrefix.substring(prefix.length());
    }

    /**
     * Return suggestions based on this auto-complete object's possibilities.
     * Suggestions are only possible when there are multiple auto-complete possibilities (1 or more).
     *
     * @return A {@code present} value when suggestions exists for this auto-complete (there are 1 or more possibilities).
     */
    public Opt<Suggestions> getSuggestions() {
        if (possibilities.size() <= 1) {
            // No suggestions if there are no possibilities, or if only 1 possibility.
            return Opt.absent();
        }

        // There are at least 2 possibilities, suggestions are available.
        final Suggestions.Builder builder = new Suggestions.Builder();
        for (Entry<String, CliValueType> entry : possibilities.entrySet()) {
            builder.addSuggestion(entry.getValue(), entry.getKey());
        }
        return Opt.of(builder.build());
    }
}