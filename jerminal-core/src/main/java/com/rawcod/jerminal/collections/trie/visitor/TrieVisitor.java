package com.rawcod.jerminal.collections.trie.visitor;

/**
 * User: ykrasik
 * Date: 04/01/14
 */
public interface TrieVisitor<T> {
    void visit(String word, T value);
}
