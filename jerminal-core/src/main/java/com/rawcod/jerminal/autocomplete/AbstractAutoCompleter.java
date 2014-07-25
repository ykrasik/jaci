package com.rawcod.jerminal.autocomplete;

import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 17:47
 */
public abstract class AbstractAutoCompleter<T> {
    private final Trie<T> words;

    protected AbstractAutoCompleter(Trie<T> words) {
        this.words = words;
    }

    public AutoCompleteReturnValue autoComplete(String prefix, Predicate<T> filter) {
        final List<String> possibleWords = words.getWordsFromPrefixWithFilter(prefix, filter);

        // No words are reachable with this prefix and filter.
        if (possibleWords.isEmpty()) {
            // Give a meaningful error message.
            final AutoCompleteReturnValueFailure failure = noPossibleWords(prefix);
            return AutoCompleteReturnValue.failure(failure);
        }

        final AutoCompleteReturnValue success;
        if (possibleWords.size() == 1) {
            // Only a single word is reachable with this prefix and filter.
            final String possibility = possibleWords.get(0);
            final String autoCompleteAddition = getAutoCompleteAddition(prefix, possibility);
            success = AutoCompleteReturnValue.successSingle(autoCompleteAddition);
        } else {
            // Multiple words are reachable with this prefix and filter.
            final String longestPrefix = words.getLongestPrefixWithFilter(prefix, filter);
            final String autoCompleteAddition = getAutoCompleteAddition(prefix, longestPrefix);
            success = AutoCompleteReturnValue.successMultiple(autoCompleteAddition, possibleWords);
        }
        return success;
    }

    private String getAutoCompleteAddition(String rawArg, String autoCompletedArg) {
        return autoCompletedArg.substring(rawArg.length());
    }

    protected abstract AutoCompleteReturnValueFailure noPossibleWords(String prefix);
}
