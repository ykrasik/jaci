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

package com.github.ykrasik.jerminal.collections.trie.node;

import java.util.Map;

/**
 * Utilities for working with {@link TrieNode}s.
 *
 * @author Yevgeny Krasik
 */
final class TrieNodeUtils {
    private TrieNodeUtils() {
    }

    /**
     * Returns the child of the map at lowerCase 'c' or upperCase 'c', in that order.
     */
    public static <T> T getCaseInsensitive(Map<Character, T> map, char c) {
        T value = map.get(Character.toLowerCase(c));
        if (value == null) {
            value = map.get(Character.toUpperCase(c));
        }
        return value;
    }
}
