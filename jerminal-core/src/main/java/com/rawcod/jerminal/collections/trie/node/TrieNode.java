package com.rawcod.jerminal.collections.trie.node;

/**
 * User: ykrasik
 * Date: 23/07/2014
 * Time: 20:26
 */
public interface TrieNode<T> {
    int numChildren();
    boolean isEmpty();

    boolean isWord();
    String getPrefix();

    T getValue();
    T setValue(T value);

    TrieNode<T> getChild(char c);
    TrieNode<T> getOrCreateChild(char c);

    Iterable<TrieNode<T>> getChildren();
}
