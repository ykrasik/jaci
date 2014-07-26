package com.rawcod.jerminal.autocomplete;

import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.ReadOnlyTrie;
import com.rawcod.jerminal.collections.trie.Tries;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 17:47
 */
public class AutoCompleter<T> {
    private final ReadOnlyTrie<T> words;

    public AutoCompleter(ReadOnlyTrie<T> words) {
        this.words = words;
    }

    public AutoCompleteReturnValue autoComplete(String prefix, Predicate<T> filter) {
        final List<String> possibleWords = Tries.getWordsFromPrefixWithFilter(words, prefix, filter);
        if (possibleWords.isEmpty()) {
            // No words are reachable with this prefix and filter.
            return AutoCompleteErrors.noPossibleValuesNoInfo();
        }

        final AutoCompleteReturnValue success;
        if (possibleWords.size() == 1) {
            // Only a single word is reachable with this prefix and filter.
            final String possibility = possibleWords.get(0);
            final String autoCompleteAddition = getAutoCompleteAddition(prefix, possibility);
            success = AutoCompleteReturnValue.successSingle(autoCompleteAddition);
        } else {
            // Multiple words are reachable with this prefix and filter.
            final String longestPrefix = Tries.getLongestPrefixFromPrefixAndFilter(words, prefix, filter);
            final String autoCompleteAddition = getAutoCompleteAddition(prefix, longestPrefix);
            success = AutoCompleteReturnValue.successMultiple(autoCompleteAddition, possibleWords);
        }
        return success;
    }

    private String getAutoCompleteAddition(String rawArg, String autoCompletedArg) {
        return autoCompletedArg.substring(rawArg.length());
    }
}
