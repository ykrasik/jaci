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
 * Trie related utilities.
 *
 * @author Yevgeny Krasik
 */
public final class Tries {
    private Tries() {
    }

    /**
     * Sometimes a Trie's values aren't important, only the words matter.
     * Convenience method that associates each word with a constant value.
     *
     * @param words Words to put in the Trie.
     * @return A Trie where each word is present in the Trie, but associated to some constant value.
     */
    public static Trie<String> toStringTrie(String... words) {
        return toStringTrie(Arrays.asList(words));
    }

    /**
     * Sometimes a Trie's values aren't important, only the words matter.
     * Convenience method that associates each word with a constant value.
     *
     * @param words Words to put in the Trie.
     * @return A Trie where each word is present in the Trie, but associated to some constant value.
     */
    public static Trie<String> toStringTrie(List<String> words) {
        Trie<String> trie = new TrieImpl<>();
        for (String word : words) {
            trie = trie.add(word, "");
        }
        return trie;
    }
}
