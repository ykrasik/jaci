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
import com.github.ykrasik.jaci.param.BooleanParamDef;
import com.github.ykrasik.jaci.util.function.MoreSuppliers;
import com.github.ykrasik.jaci.util.function.Spplr;
import com.github.ykrasik.jaci.util.opt.Opt;
import com.github.ykrasik.jaci.util.trie.Trie;
import com.github.ykrasik.jaci.util.trie.TrieBuilder;

/**
 * A {@link CliParam} that parses boolean values.
 * Specifically, only 'true' and 'false' (case insensitive).
 *
 * If the parameter is optional, it can be treated as a flag:
 * This is a special case when calling the parameter by name, but not providing a value as the 2nd argument.
 * In this case, the optional boolean parameter will just receive the value that is the inverse of its default value.
 *
 * Example:
 *   There is an optional boolean parameter called 'r', with a default value of {@code false}.
 *   When calling this parameter by name, the call-by-name syntax requires that a boolean value be provided after the
 *   parameter name. However, if we only write '-r' without following with a boolean value, the parameter will
 *   receive the value {@code true} - the inverse of it's default value.
 *
 * @author Yevgeny Krasik
 */
public class BooleanCliParam extends AbstractCliParam<Boolean> {
    private static final Trie<CliValueType> NON_NULLABLE_VALUES = new TrieBuilder<CliValueType>()
        .add("true", CliValueType.COMMAND_PARAM_VALUE)
        .add("false", CliValueType.COMMAND_PARAM_VALUE)
        .build();

    private static final Trie<CliValueType> NULLABLE_VALUES = NON_NULLABLE_VALUES
        .add("null", CliValueType.COMMAND_PARAM_VALUE);

    public BooleanCliParam(Identifier identifier, Opt<Spplr<Boolean>> defaultValueSupplier, boolean nullable) {
        super(identifier, defaultValueSupplier, nullable);
    }

    @Override
    protected String getValueTypeName() {
        return "boolean";
    }

    @Override
    public Boolean noValue() throws ParseException {
        // If this boolean parameter is optional, we can treat this case as if it is a flag -
        // return the inverse of the default value.
        return !unbound();
    }

    @Override
    public Boolean parseNonNull(String arg) throws ParseException {
        if (!NON_NULLABLE_VALUES.contains(arg.toLowerCase())) {
            throw invalidParamValue(arg);
        }
        return Boolean.parseBoolean(arg);
    }

    @Override
    public AutoComplete autoComplete(String prefix) throws ParseException {
        final Trie<CliValueType> possibilities = (nullable ? NULLABLE_VALUES : NON_NULLABLE_VALUES).subTrie(prefix.toLowerCase());
        return new AutoComplete(prefix, possibilities);
    }

    /**
     * Construct a CLI boolean parameter from a {@link BooleanParamDef}.
     *
     * @param def BooleanParamDef to construct a CLI boolean parameter from.
     * @return A CLI boolean parameter constructed from the BooleanParamDef.
     */
    public static BooleanCliParam fromDef(BooleanParamDef def) {
        return new BooleanCliParam(def.getIdentifier(), def.getDefaultValueSupplier(), def.isNullable());
    }

    /**
     * Construct an optional CLI boolean parameter with the given default value.
     *
     * @param identifier Parameter identifier.
     * @param defaultValue Default value to be used by the parameter if it isn't explicitly bound.
     * @param nullable Whether the parameter is nullable.
     * @return A CLI boolean parameter constructed from the given parameters.
     */
    public static BooleanCliParam optional(Identifier identifier, boolean defaultValue, boolean nullable) {
        return new BooleanCliParam(identifier, Opt.of(MoreSuppliers.of(defaultValue)), nullable);
    }
}
