package com.rawcod.jerminal.command.parameters.string;

import com.google.common.base.Supplier;
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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 18:21
 */
public class DynamicStringParam extends AbstractMandatoryCommandParam {
    private final Supplier<List<String>> valuesSupplier;

    public DynamicStringParam(String name, String description, Supplier<List<String>> valuesSupplier) {
        super(name, description);
        this.valuesSupplier = checkNotNull(valuesSupplier, "valuesSupplier is null!");
    }

    @Override
    protected String getExternalFormType() {
        return "string";
    }

    @Override
    protected ParseParamValueReturnValue parse(String rawValue, ParseParamContext context) {
        final Trie<String> values = getValues();
        final StringParamValueParser parser = new StringParamValueParser(values, getName());
        return parser.parse(rawValue);
    }

    @Override
    protected AutoCompleteReturnValue autoComplete(String prefix, ParseParamContext context) {
        final Trie<String> values = getValues();
        final WordTrie wordTrie = Tries.getWordTrie(values, prefix);
        if (wordTrie.isEmpty()) {
            // Give a meaningful error message;
            return AutoCompleteErrors.noPossibleValuesForParamWithPrefix(getName(), prefix);
        }

        return AutoCompleteUtils.autoComplete(prefix, wordTrie);
    }

    private Trie<String> getValues() {
        return Tries.toTrie(valuesSupplier.get());
    }
}