/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.api.command.parameter.string;

import com.google.common.base.Supplier;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.internal.command.parameter.AbstractMandatoryCommandParam;
import com.rawcod.jerminal.exception.ParseException;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteMappers;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteReturnValue;
import com.rawcod.jerminal.returnvalue.autocomplete.AutoCompleteType;
import com.rawcod.jerminal.returnvalue.parse.ParseErrors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link com.github.ykrasik.jerminal.api.command.parameter.CommandParam CommandParam} that parses string values.<br>
 * It can be configured in 3 modes - It can either accept any string, be constrained to a pre-determined set of strings
 * or be constrained to a set of strings that is supplied dynamically at runtime.
 *
 * @author Yevgeny Krasik
 */
// FIXME: This is incorrect. A situation where a dynamicSupplier returns an empty values list, any string will be accepted.
public class StringParam extends AbstractMandatoryCommandParam<String> {
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
    public String parse(String rawValue) throws ParseException {
        final Trie<String> values = getValues();

        // If the possible values trie is empty, all values are accepted.
        // If it isn't, rawValue must be contained in the possible values trie.
        if (values.isEmpty() || values.contains(rawValue)) {
            return rawValue;
        }

        // This string param is constrained by the values it can receive,
        // and rawValue isn't contained in the possible values trie.
        throw ParseErrors.invalidParamValue(getName(), rawValue);
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix) throws ParseException {
        final Trie<String> values = getValues();
        final Trie<String> prefixTrie = values.subTrie(prefix);
        final Trie<AutoCompleteType> possibilities = prefixTrie.map(AutoCompleteMappers.commandParamValueStringMapper());
        return new AutoCompleteReturnValue(prefix, possibilities);
    }

    private Trie<String> getValues() {
        return valuesSupplier.get();
    }
}
