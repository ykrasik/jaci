/******************************************************************************
 * Copyright (C) 2016 Yevgeny Krasik                                          *
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
import com.github.ykrasik.jaci.param.EnumParamDef;
import com.github.ykrasik.jaci.util.function.Spplr;
import com.github.ykrasik.jaci.util.opt.Opt;
import com.github.ykrasik.jaci.util.trie.Trie;
import com.github.ykrasik.jaci.util.trie.Tries;

/**
 * A {@link CliParam} that parses enum values.
 *
 * @author Yevgeny Krasik
 */
public class EnumCliParam<E extends Enum<E>> extends AbstractCliParam<E> {
    private final Trie<E> enumValues;

    public EnumCliParam(Identifier identifier, Opt<Spplr<E>> defaultValueSupplier, Class<E> enumClass) {
        super(identifier, defaultValueSupplier);
        this.enumValues = Tries.enumTrie(enumClass);
    }

    @Override
    public E parse(String arg) throws ParseException {
        // TODO: Handle case sensitivity?
        final Opt<E> value = enumValues.get(arg);
        if (value.isPresent()) {
            return value.get();
        }

        throw invalidParamValue(arg);
    }

    @Override
    public AutoComplete autoComplete(String prefix) throws ParseException {
        final Trie<CliValueType> possibilities = enumValues.subTrie(prefix).mapValues(CliValueType.COMMAND_PARAM_VALUE.<E>getMapper());
        return new AutoComplete(prefix, possibilities);
    }

    @Override
    protected String getValueTypeName() {
        return "enum";
    }

    /**
     * Construct a CLI enum parameter from an {@link EnumParamDef}.
     *
     * @param def EnumParamDef to construct a CLI enum parameter from.
     * @return A CLI {@code Enum} parameter constructed from the EnumParamDef.
     */
    public static <E extends Enum<E>> EnumCliParam<E> fromDef(EnumParamDef<E> def) {
        return new EnumCliParam<>(def.getIdentifier(), def.getDefaultValueSupplier(), def.getEnumClass());
    }
}
