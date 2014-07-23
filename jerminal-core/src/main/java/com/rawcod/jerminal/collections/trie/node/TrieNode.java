package com.rawcod.jerminal.collections.trie.node;

/**
 * User: ykrasik
 * Date: 23/07/2014
 * Time: 20:26
 */
public interface TrieNode<T> {
    int numChildren();
    boolean isEmpty();

    char getCharacter();
    String getPrefix();

    boolean isWord();
    T getValue();
    T setValue(T value);

    TrieNode<T> getChild(char c);
    void setChild(char c, TrieNode<T> child);

    Iterable<TrieNode<T>> getChildren();
}
