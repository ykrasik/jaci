package com.rawcod.jerminal.collections.trie;

/**
 * User: ykrasik
 * Date: 04/01/14
 */
public interface TrieVisitor<V> {
    void visit(String word, V value);
}
