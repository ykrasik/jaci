package com.rawcod.jerminal.collections.trie.node;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TrieNodeImpl<T> implements TrieNode<T> {
    private final char character;
    private final T value;
    private final Map<Character, TrieNode<T>> children;

    public TrieNodeImpl(char character, T value, Map<Character, TrieNode<T>> children) {
        this.character = character;
        this.value = value;
        this.children = children;
    }

    @Override
    public boolean isWord() {
        return value != null;
    }

    @Override
    public boolean isEmpty() {
        return !isWord() && children.isEmpty();
    }

    @Override
    public char getCharacter() {
        return character;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public TrieNode<T> getChild(char c) {
        return TrieNodeUtils.getCaseInsensitive(children, c);
    }

    @Override
    public Collection<TrieNode<T>> getChildren() {
        return Collections.unmodifiableCollection(children.values());
    }

    @Override
    public String toString() {
        return String.valueOf(character);
    }
}
