package com.rawcod.jerminal.collections.trie.node;

import java.util.Collection;

/**
 * User: ykrasik
 * Date: 23/07/2014
 * Time: 20:26
 */
public interface TrieNode<T> {
    boolean isWord();
    boolean isEmpty();

    char getCharacter();
    T getValue();

    TrieNode<T> getChild(char c);
    Collection<TrieNode<T>> getChildren();
}
