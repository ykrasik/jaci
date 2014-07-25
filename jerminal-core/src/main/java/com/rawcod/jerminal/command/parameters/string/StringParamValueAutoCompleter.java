package com.rawcod.jerminal.command.parameters.string;

import com.rawcod.jerminal.autocomplete.AbstractAutoCompleter;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 18:17
 */
public class StringParamValueAutoCompleter extends AbstractAutoCompleter<String> {
    private final String paramName;

    public StringParamValueAutoCompleter(Trie<String> words, String paramName) {
        super(words);
        this.paramName = paramName;
    }

    @Override
    protected AutoCompleteReturnValueFailure noPossibleWords(String prefix) {
        return AutoCompleteReturnValueFailure.from(
            AutoCompleteError.NO_POSSIBLE_VALUES,
            "AutoComplete error: No values are possible for param '%s' with prefix: '%s'", paramName, prefix
        );
    }
}
