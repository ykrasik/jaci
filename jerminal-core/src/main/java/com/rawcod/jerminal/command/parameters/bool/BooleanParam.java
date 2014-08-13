package com.rawcod.jerminal.command.parameters.bool;

import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieBuilder;
import com.rawcod.jerminal.command.parameters.AbstractMandatoryCommandParam;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteMappers;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteType;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class BooleanParam extends AbstractMandatoryCommandParam<Boolean> {
    private static final Trie<String> VALUES = new TrieBuilder<String>().add("true", "").add("false", "").build();

    public BooleanParam(String name, String description) {
        super(name, description);
    }

    @Override
    protected String getExternalFormType() {
        return "bool";
    }

    @Override
    public Boolean parse(String rawValue, ParseParamContext context) throws ParseException {
        if (VALUES.contains(rawValue)) {
            return Boolean.parseBoolean(rawValue);
        }

        // This string param is constrained by the values it can receive,
        // and rawValue isn't contained in the possible values trie.
        throw ParseErrors.invalidParamValue(getName(), rawValue);
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix, ParseParamContext context) throws ParseException {
        final Trie<AutoCompleteType> possibilities = VALUES.subTrie(prefix).map(AutoCompleteMappers.commandParamValueStringMapper());
        return new AutoCompleteReturnValue(prefix, possibilities);
    }
}
