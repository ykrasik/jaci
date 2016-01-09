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

package com.github.ykrasik.jaci.cli.param;

import com.github.ykrasik.jaci.Identifier;
import com.github.ykrasik.jaci.cli.assist.AutoComplete;
import com.github.ykrasik.jaci.cli.assist.CliValueType;
import com.github.ykrasik.jaci.cli.exception.ParseException;
import com.github.ykrasik.jaci.param.StringParamDef;
import com.github.ykrasik.jaci.util.function.Func;
import com.github.ykrasik.jaci.util.function.MoreSuppliers;
import com.github.ykrasik.jaci.util.function.Spplr;
import com.github.ykrasik.jaci.util.opt.Opt;
import com.github.ykrasik.jaci.util.trie.Trie;
import com.github.ykrasik.jaci.util.trie.TrieBuilder;

import java.util.List;
import java.util.Objects;

/**
 * A {@link CliParam} that parses string values.
 *
 * @author Yevgeny Krasik
 */
public class StringCliParam extends AbstractCliParam<String> {
    private final Spplr<Trie<CliValueType>> valuesSupplier;

    public StringCliParam(Identifier identifier,
                          Opt<Spplr<String>> defaultValueSupplier,
                          Spplr<List<String>> valuesSupplier) {
        super(identifier, defaultValueSupplier);

        // If the supplier is a const supplier type (supplies a constant or cached value), the returned supplier
        // will also cache the result and not re-calculate it on every call.
        this.valuesSupplier = MoreSuppliers.map(Objects.requireNonNull(valuesSupplier, "valuesSupplier"), new Func<List<String>, Trie<CliValueType>>() {
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
    protected String getValueTypeName() {
        return "string";
    }

    @Override
    public String parse(String arg) throws ParseException {
        final Trie<CliValueType> values = getValues();

        // If the values trie is empty, all values are accepted.
        // If it isn't, arg must be contained in the possible values trie.
        // TODO: Should this be case insensitive?
        if (values.isEmpty() || values.contains(arg)) {
            return arg;
        }

        // This string param is constrained by the values it can receive,
        // and arg isn't contained in the possible values trie.
        throw invalidParamValue(arg);
    }

    @Override
    public AutoComplete autoComplete(String prefix) throws ParseException {
        // FIXME: Add proper support for auto completing quoted strings.
        final Trie<CliValueType> possibilities = getValues().subTrie(prefix);
        return new AutoComplete(prefix, possibilities);
    }

    private Trie<CliValueType> getValues() {
        return valuesSupplier.get();
    }

    /**
     * Construct a CLI string parameter from a {@link StringParamDef}.
     *
     * @param def StringParamDef to construct a CLI string parameter from.
     * @return A CLI string parameter constructed from the StringParamDef.
     */
    public static StringCliParam fromDef(StringParamDef def) {
        return new StringCliParam(def.getIdentifier(), def.getDefaultValueSupplier(), def.getValuesSupplier());
    }
}
