package com.rawcod.jerminal.collections.trie.node;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TrieNodeImpl<T> implements ValueTrieNode<T> {
    private final char character;
    private final Map<Character, ValueTrieNode<T>> children;

    private T value;

    public TrieNodeImpl() {
        this((char) 0);
    }

    public TrieNodeImpl(char character) {
        this.character = character;
        this.children = new HashMap<>(1);
    }

    @Override
    public char getCharacter() {
        return character;
    }

    @Override
    public boolean isWord() {
        return value != null;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public TrieNode getChild(char c) {
        TrieNode child = children.get(Character.toLowerCase(c));
        if (child == null) {
            child = children.get(Character.toUpperCase(c));
        }
        return child;
    }

    @Override
    public void setChild(char c, ValueTrieNode<T> child) {
        if (getChild(c) != null) {
            final String message = String.format("Node already has a child at '%c'", c);
            throw new IllegalArgumentException(message);
        }
        children.put(c, child);
    }

    @Override
    public Collection<TrieNode> getChildren() {
        return Collections.<TrieNode>unmodifiableCollection(children.values());
    }

    @Override
    public String toString() {
        return String.valueOf(character);
    }
}
