/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jemi.cli.param;

import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jemi.Identifier;
import com.github.ykrasik.jemi.param.StringParamDef;
import com.github.ykrasik.jemi.cli.assist.AutoComplete;
import com.github.ykrasik.jemi.cli.assist.CliValueType;
import com.github.ykrasik.jemi.util.function.Function;
import com.github.ykrasik.jemi.util.function.Supplier;
import com.github.ykrasik.jemi.util.function.Suppliers;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.trie.Trie;
import com.github.ykrasik.jemi.util.trie.TrieBuilder;
import lombok.NonNull;

import java.util.List;

/**
 * A {@link CliParam} that parses string values.<br>
 * Can be configured in 3 modes - can either accept any string, be constrained to a pre-determined set of strings
 * or be constrained to a set of strings that is supplied dynamically at runtime.
 *
 * @author Yevgeny Krasik
 */
// FIXME: Add proper support for auto completing quoted strings.
public class StringCliParam extends AbstractCliParam<String> {
    private final Supplier<Trie<CliValueType>> valuesSupplier;

    public StringCliParam(Identifier identifier,
                          Opt<Supplier<String>> defaultValueSupplier,
                          @NonNull Supplier<List<String>> valuesSupplier) {
        super(identifier, defaultValueSupplier);

        // If the supplier is a const supplier type (supplies a constant or cached value), the returned supplier
        // will also cache the result and not re-calculate it on every call.
        this.valuesSupplier = Suppliers.transform(valuesSupplier, new Function<List<String>, Trie<CliValueType>>() {
            @Override
            public Trie<CliValueType> apply(List<String> values) {
                return createValuesTrie(values);
            }
        });
    }

    private Trie<CliValueType> createValuesTrie(List<String> values) {
        final TrieBuilder<CliValueType> builder = new TrieBuilder<>();
        for (String value : values) {
            builder.add(value, CliValueType.COMMAND_PARAM_VALUE);
        }
        return builder.build();
    }

    @Override
    protected String getParamTypeName() {
        return "string";
    }

    @Override
    public String parse(@NonNull String rawValue) throws ParseException {
        final Trie<CliValueType> values = getValues();

        // If the values trie is empty, all values are accepted.
        // If it isn't, rawValue must be contained in the possible values trie.
        // TODO: SHould this be case insensitive?
        if (values.isEmpty() || values.contains(rawValue)) {
            return rawValue;
        }

        // This string param is constrained by the values it can receive,
        // and rawValue isn't contained in the possible values trie.
        throw invalidParamValue(rawValue);
    }

    @Override
    public AutoComplete autoComplete(@NonNull String prefix) throws ParseException {
        final Trie<CliValueType> possibilities = getValues().subTrie(prefix);
        return new AutoComplete(prefix, possibilities);
    }

    private Trie<CliValueType> getValues() {
        return valuesSupplier.get();
    }

    // TODO: JavaDoc
    public static StringCliParam fromDef(@NonNull StringParamDef def) {
        return new StringCliParam(def.getIdentifier(), def.getDefaultValueSupplier(), def.getValuesSupplier());
    }
}
