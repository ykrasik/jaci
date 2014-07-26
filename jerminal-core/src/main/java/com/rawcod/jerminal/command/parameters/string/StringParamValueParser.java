package com.rawcod.jerminal.command.parameters.string;

import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 18:44
 */
public class StringParamValueParser {
    private final Trie<String> values;
    private final String paramName;

    public StringParamValueParser(Trie<String> values, String paramName) {
        this.values = values;
        this.paramName = paramName;
    }

    public ParseParamValueReturnValue parse(String rawValue) {
        // If the possible values trie is empty, all values are accepted.
        if (values.isEmpty()) {
            return ParseParamValueReturnValue.success(rawValue);
        }

        // This string param is constrained by the values it can receive.
        // rawValue must be contained in the possible values trie.
        if (!values.contains(rawValue)) {
            return ParseErrors.invalidParamValue(paramName, rawValue);
        }

        return ParseParamValueReturnValue.success(rawValue);
    }
}
