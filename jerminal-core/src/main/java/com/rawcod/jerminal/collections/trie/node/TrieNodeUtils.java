package com.rawcod.jerminal.collections.trie.node;

import java.util.Map;

/**
 * User: ykrasik
 * Date: 11/08/2014
 * Time: 21:34
 */
final class TrieNodeUtils {
    private TrieNodeUtils() {
    }

    public static <T> T getCaseInsensitive(Map<Character, T> map, char c) {
        T value = map.get(Character.toLowerCase(c));
        if (value == null) {
            value = map.get(Character.toUpperCase(c));
        }
        return value;
    }
}
