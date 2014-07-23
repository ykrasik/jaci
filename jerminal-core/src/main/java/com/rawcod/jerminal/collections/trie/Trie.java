package com.rawcod.jerminal.collections.trie;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 10:52
 */
public interface Trie<T> extends ReadOnlyTrie<T> {
    void put(String word, T value);

    T remove(String word);
}
