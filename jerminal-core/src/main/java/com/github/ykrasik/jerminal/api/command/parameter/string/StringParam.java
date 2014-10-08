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

import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.internal.command.parameter.AbstractMandatoryCommandParam;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteReturnValue;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteType;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link com.github.ykrasik.jerminal.api.command.parameter.CommandParam CommandParam} that parses string values.<br>
 * It can be configured in 3 modes - It can either accept any string, be constrained to a pre-determined set of strings
 * or be constrained to a set of strings that is supplied dynamically at runtime.
 *
 * @author Yevgeny Krasik
 */
// FIXME: This is incorrect. A situation where a dynamicSupplier returns an empty values list, any string will be accepted.
// FIXME: Add proper support for quoted strings.
public class StringParam extends AbstractMandatoryCommandParam<String> {
    private static final Function<String, AutoCompleteType> AUTO_COMPLETE_TYPE_MAPPER = new Function<String, AutoCompleteType>() {
        @Override
        public AutoCompleteType apply(String input) {
            return AutoCompleteType.COMMAND_PARAM_VALUE;
        }
    };

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
        throw invalidParamValue(rawValue);
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix) throws ParseException {
        final Trie<String> values = getValues();
        final Trie<String> prefixTrie = values.subTrie(prefix);
        final Trie<AutoCompleteType> possibilities = prefixTrie.map(AUTO_COMPLETE_TYPE_MAPPER);
        return new AutoCompleteReturnValue(prefix, possibilities);
    }

    private Trie<String> getValues() {
        return valuesSupplier.get();
    }

    private ParseException invalidParamValue(String value) {
        return new ParseException(
            ParseError.INVALID_PARAM_VALUE,
            "Invalid value for string parameter '%s': '%s'", getName(), value
        );
    }
}
