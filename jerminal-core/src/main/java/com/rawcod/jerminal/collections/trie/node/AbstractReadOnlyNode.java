package com.rawcod.jerminal.collections.trie.node;

/**
 * User: ykrasik
 * Date: 26/07/2014
 * Time: 13:53
 */
public abstract class AbstractReadOnlyNode<T> implements TrieNode<T> {
    @Override
    public void setValue(T value) {
        throw new UnsupportedOperationException("Node is read-only!");
    }

    @Override
    public void setChild(char c, TrieNode<T> child) {
        throw new UnsupportedOperationException("Node is read-only!");
    }
}
