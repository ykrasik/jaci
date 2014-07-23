package com.rawcod.jerminal.collections.trie.node;

/**
 * User: ykrasik
 * Date: 23/07/2014
 * Time: 22:52
 */
public class UnionTrieNodeImpl<T> implements TrieNode<T> {
    @Override
    public int numChildren() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isWord() {
        return false;
    }

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public T getValue() {
        return null;
    }

    @Override
    public T setValue(T value) {
        return null;
    }

    @Override
    public TrieNode<T> getChild(char c) {
        return null;
    }

    @Override
    public TrieNode<T> getOrCreateChild(char c) {
        return null;
    }

    @Override
    public Iterable<TrieNode<T>> getChildren() {
        return null;
    }
}
