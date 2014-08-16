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

package com.github.ykrasik.jerminal.internal.returnvalue;

import com.google.common.base.Objects;
import com.github.ykrasik.jerminal.collections.trie.Trie;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The return value of an auto complete operation.<br>
 * Contains the prefix that was auto completed and a trie of possible words from this prefix,
 * with each word's type as it's value.
 *
 * @author Yevgeny Krasik
 */
// TODO: Find a way to get rid of AutoCompleteType and replace it with a char.
// TODO: Find a way to get rid of prefix.
public class AutoCompleteReturnValue {
    private final String prefix;
    private final Trie<AutoCompleteType> possibilities;

    public AutoCompleteReturnValue(String prefix, Trie<AutoCompleteType> possibilities) {
        this.prefix = checkNotNull(prefix, "prefix");
        this.possibilities = checkNotNull(possibilities, "possibilities");
    }

    public String getPrefix() {
        return prefix;
    }

    public Trie<AutoCompleteType> getPossibilities() {
        return possibilities;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("prefix", prefix)
            .add("possibilities", possibilities)
            .toString();
    }
}