package com.rawcod.jerminal.autocomplete;

import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.command.parameters.CommandParam;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteError;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValueFailure;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 18:04
 */
public class CommandParamNameAutoCompleter extends AbstractAutoCompleter<CommandParam> {
    public CommandParamNameAutoCompleter(Trie<CommandParam> commands) {
        super(commands);
    }

    @Override
    protected AutoCompleteReturnValueFailure noPossibleWords(String prefix) {
        return AutoCompleteReturnValueFailure.from(
            AutoCompleteError.NO_POSSIBLE_VALUES,
            "AutoComplete error: No unbound param starts with '%s'", prefix
        );
    }
}
