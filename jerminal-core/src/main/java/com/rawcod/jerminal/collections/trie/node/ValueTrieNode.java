package com.rawcod.jerminal.collections.trie.node;

/**
 * User: ykrasik
 * Date: 05/08/2014
 * Time: 00:04
 */
public interface ValueTrieNode<T> extends TrieNode {
    T getValue();
    void setValue(T value);

    void setChild(char c, ValueTrieNode<T> child);
}
