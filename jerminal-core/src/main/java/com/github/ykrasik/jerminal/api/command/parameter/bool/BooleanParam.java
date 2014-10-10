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

package com.github.ykrasik.jerminal.api.command.parameter.bool;

import com.github.ykrasik.jerminal.api.exception.ParseError;
import com.github.ykrasik.jerminal.collections.trie.Trie;
import com.github.ykrasik.jerminal.collections.trie.TrieImpl;
import com.github.ykrasik.jerminal.internal.command.parameter.AbstractMandatoryCommandParam;
import com.github.ykrasik.jerminal.internal.exception.ParseException;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteReturnValue;
import com.github.ykrasik.jerminal.internal.returnvalue.AutoCompleteType;
import com.google.common.base.Function;

/**
 * A {@link com.github.ykrasik.jerminal.api.command.parameter.CommandParam CommandParam} that parses boolean values.<br>
 * Specifically, only 'true' and 'false'.
 *
 * @author Yevgeny Krasik
 */
public class BooleanParam extends AbstractMandatoryCommandParam<Boolean> {
    private static final Trie<String> VALUES = new TrieImpl<String>().add("true", "").add("false", "");

    private static final Function<String, AutoCompleteType> AUTO_COMPLETE_TYPE_MAPPER = new Function<String, AutoCompleteType>() {
        @Override
        public AutoCompleteType apply(String input) {
            return AutoCompleteType.COMMAND_PARAM_VALUE;
        }
    };

    public BooleanParam(String name, String description) {
        super(name, description);
    }

    @Override
    protected String getExternalFormType() {
        return "bool";
    }

    @Override
    public Boolean parse(String rawValue) throws ParseException {
        if (VALUES.contains(rawValue)) {
            return Boolean.parseBoolean(rawValue);
        }

        throw invalidParamValue(rawValue);
    }

    @Override
    public AutoCompleteReturnValue autoComplete(String prefix) throws ParseException {
        final Trie<AutoCompleteType> possibilities = VALUES.subTrie(prefix).map(AUTO_COMPLETE_TYPE_MAPPER);
        return new AutoCompleteReturnValue(prefix, possibilities);
    }

    private ParseException invalidParamValue(String value) {
        return new ParseException(
            ParseError.INVALID_PARAM_VALUE,
            "Invalid value for boolean parameter '%s': '%s'", getName(), value
        );
    }
}
