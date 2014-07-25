package com.rawcod.jerminal.command.parameters.string;

import com.google.common.base.Predicates;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.command.parameters.AbstractCommandParam;
import com.rawcod.jerminal.command.parameters.ParamParseContext;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;
import com.rawcod.jerminal.util.TrieUtils;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 16:36
 */
public class StringParam extends AbstractCommandParam {
    private final StringParamValueParser parser;
    private final StringParamValueAutoCompleter autoCompleter;

    public StringParam(String name, String description, List<String> possibleValues) {
        super(name, description);

        final Trie<String> values = TrieUtils.toTrie(possibleValues);
        this.parser = new StringParamValueParser(values, name);
        this.autoCompleter = new StringParamValueAutoCompleter(values, name);
    }

    @Override
    protected ParseParamValueReturnValue parse(String rawValue, ParamParseContext context) {
        return parser.parse(rawValue);
    }

    @Override
    protected AutoCompleteReturnValue autoComplete(String prefix, ParamParseContext context) {
        return autoCompleter.autoComplete(prefix, Predicates.<String>alwaysTrue());
    }

    @Override
    public String toString() {
        return String.format("{%s: String}", getName());
    }
}
