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
import com.github.ykrasik.jemi.core.Identifier;
import com.github.ykrasik.jemi.core.param.BooleanParamDef;
import com.github.ykrasik.jerminal.old.assist.AutoCompleteReturnValue;
import com.github.ykrasik.jerminal.old.assist.CliValueType;
import com.github.ykrasik.jemi.util.function.Supplier;
import com.github.ykrasik.jemi.util.function.Suppliers;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.trie.Trie;
import com.github.ykrasik.jemi.util.trie.TrieBuilder;
import lombok.NonNull;

/**
 * A {@link CliParam} that parses boolean values.<br>
 * Specifically, only 'true' and 'false' (case insensitive).
 *
 * @author Yevgeny Krasik
 */
public class BooleanCliParam extends AbstractCliParam<Boolean> {
    private static final Trie<CliValueType> VALUES = new TrieBuilder<CliValueType>()
        .add("true", CliValueType.COMMAND_PARAM_VALUE)
        .add("false", CliValueType.COMMAND_PARAM_VALUE)
        .build();

    public BooleanCliParam(Identifier identifier, Opt<Supplier<Boolean>> defaultValueSupplier) {
        super(identifier, defaultValueSupplier);
    }

    @Override
    protected String getParamTypeName() {
        return "boolean";
    }

    @Override
    public Object noValue() throws ParseException {
        // If this boolean parameter is optional, we can treat this case as if it is a flag -
        // return the inverse of the default value.
        return !unbound();
    }

    @Override
    public Boolean parse(@NonNull String rawValue) throws ParseException {
        if (VALUES.contains(rawValue.toLowerCase())) {
            return Boolean.parseBoolean(rawValue);
        }

        throw invalidParamValue(rawValue);
    }

    @Override
    public AutoCompleteReturnValue autoComplete(@NonNull String prefix) throws ParseException {
        final Trie<CliValueType> possibilities = VALUES.subTrie(prefix.toLowerCase());
        return new AutoCompleteReturnValue(prefix, possibilities);
    }

    // TODO: JavaDoc
    public static BooleanCliParam fromDef(@NonNull BooleanParamDef def) {
        return new BooleanCliParam(def.getIdentifier(), def.getDefaultValueSupplier());
    }

    // TODO: JavaDoc
    public static BooleanCliParam optional(Identifier identifier, boolean defaultValue) {
        return new BooleanCliParam(identifier, Opt.of(Suppliers.of(defaultValue)));
    }
}
