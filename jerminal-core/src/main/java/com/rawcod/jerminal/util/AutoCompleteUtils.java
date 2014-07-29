package com.rawcod.jerminal.util;

import com.rawcod.jerminal.collections.trie.WordTrie;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;

import java.util.List;

/**
 * User: ykrasik
 * Date: 29/07/2014
 * Time: 23:14
 */
public final class AutoCompleteUtils {
    private AutoCompleteUtils() {

    }

    public static AutoCompleteReturnValue autoComplete(String prefix, WordTrie words) {
        if (words.isEmpty()) {
            // No words are reachable with this prefix and filter.
            return AutoCompleteErrors.noPossibleValuesNoInfo();
        }

        final List<String> possibleWords = words.getAllWords();
        if (possibleWords.isEmpty()) {
            // No words are reachable with this prefix and filter.
            return AutoCompleteErrors.internalError("Words aren't empty, but returned an empty list?!");
        }

        final AutoCompleteReturnValue success;
        if (possibleWords.size() == 1) {
            // Only a single word in the trie.
            final String possibility = possibleWords.get(0);
            final String autoCompleteAddition = getAutoCompleteAddition(prefix, possibility);
            success = AutoCompleteReturnValue.successSingle(autoCompleteAddition);
        } else {
            // Multiple words in the trie.
            final String longestPrefix = words.getLongestPrefix();
            final String autoCompleteAddition = getAutoCompleteAddition(prefix, longestPrefix);
            success = AutoCompleteReturnValue.successMultiple(autoCompleteAddition, possibleWords);
        }
        return success;
    }

    private static String getAutoCompleteAddition(String rawArg, String autoCompletedArg) {
        return autoCompletedArg.substring(rawArg.length());
    }
}
