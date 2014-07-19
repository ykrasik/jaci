package com.rawcod.jerminal.collections.trie;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 10:52
 */
public interface Trie<V> extends ReadOnlyTrie<V> {
    void addWord(String word, V value);
}
