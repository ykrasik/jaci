package com.rawcod.jerminal.util;

import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieImpl;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/07/2014
 * Time: 18:53
 */
public final class TrieUtils {
    private TrieUtils() {
    }

    public static Trie<String> toTrie(List<String> values) {
        final Trie<String> trie = new TrieImpl<>();
        for (String value : values) {
            trie.put(value, value);
        }
        return trie;
    }
}
