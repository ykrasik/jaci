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

package com.github.ykrasik.jerminal.collections.trie;

import java.util.Arrays;
import java.util.List;

/**
 * @author Yevgeny Krasik
 */
public final class Tries {
    private Tries() {
    }

    // FIXME: JavaDoc
    public static Trie<String> toStringTrie(String... strings) {
        return toStringTrie(Arrays.asList(strings));
    }

    public static Trie<String> toStringTrie(List<String> strings) {
        Trie<String> trie = new TrieImpl<>();
        for (String string : strings) {
            trie = trie.add(string, "");
        }
        return trie;
    }
}
