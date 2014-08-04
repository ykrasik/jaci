package com.rawcod.jerminal.command.parameters.string;

import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.Tries;
import com.rawcod.jerminal.collections.trie.WordTrie;
import com.rawcod.jerminal.command.parameters.AbstractMandatoryCommandParam;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteErrors;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;
import com.rawcod.jerminal.util.AutoCompleteUtils;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 16:36
 */
public class StringParam extends AbstractMandatoryCommandParam {
    private final Trie<String> values;
    private final StringParamValueParser parser;

    public StringParam(String name, String description, List<String> possibleValues) {
        super(name, description);

        values = Tries.toTrie(possibleValues);
        this.parser = new StringParamValueParser(values, name);
    }

    @Override
    protected String getExternalFormType() {
        return "string";
    }

    @Override
    protected ParseParamValueReturnValue parse(String rawValue, ParseParamContext context) {
        return parser.parse(rawValue);
    }

    @Override
    protected AutoCompleteReturnValue autoComplete(String prefix, ParseParamContext context) {
        final WordTrie valuesTrie = Tries.getWordTrie(values, prefix);
        if (valuesTrie.isEmpty()) {
            // Give a meaningful error message;
            return AutoCompleteErrors.noPossibleValuesForParamWithPrefix(getName(), prefix);
        }

        return AutoCompleteUtils.autoComplete(prefix, valuesTrie);
    }
}
