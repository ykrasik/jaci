package com.rawcod.jerminal.collections.trie.node;

import java.util.Collection;

/**
 * User: ykrasik
 * Date: 23/07/2014
 * Time: 20:26
 */
public interface TrieNode<T> {
    char getCharacter();

    T getValue();
    void setValue(T value);

    TrieNode<T> getChild(char c);
    void setChild(char c, TrieNode<T> child);

    Collection<TrieNode<T>> getChildren();
}
