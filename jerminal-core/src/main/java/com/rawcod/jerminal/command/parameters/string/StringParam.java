package com.rawcod.jerminal.command.parameters.string;

import com.google.common.base.Supplier;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.command.parameters.AbstractMandatoryCommandParam;
import com.rawcod.jerminal.command.parameters.ParseParamContext;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteMappers;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteType;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;
import com.rawcod.jerminal.returnvalue.parse.param.ParseParamValueReturnValue;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 16:36
 */
public class StringParam extends AbstractMandatoryCommandParam {
    private final Supplier<Trie<String>> valuesSupplier;

    public StringParam(String name, String description, Supplier<Trie<String>> valuesSupplier) {
        super(name, description);
        this.valuesSupplier = checkNotNull(valuesSupplier, "valuesSupplier");
    }

    @Override
    protected String getExternalFormType() {
        return "string";
    }

    @Override
    public ParseParamValueReturnValue parse(String rawValue, ParseParamContext context) {
        final Trie<String> values = getValues();

        // If the possible values trie is empty, all values are accepted.
        // If it isn't, rawValue must be contained in the possible values trie.
        if (values.isEmpty() || values.contains(rawValue)) {
            return ParseParamValueReturnValue.success(rawValue);
        }

        // This string param is constrained by the values it can receive,
        // and rawValue isn't contained in the possible values trie.
        return ParseErrors.invalidParamValue(getName(), rawValue);
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix, ParseParamContext context) {
        final Trie<String> values = getValues();
        final Trie<AutoCompleteType> possibilities = values.subTrie(prefix).map(AutoCompleteMappers.commandParamValueStringMapper());
        return AutoCompleteReturnValue.success(prefix, possibilities);
    }

    private Trie<String> getValues() {
        return valuesSupplier.get();
    }
}
