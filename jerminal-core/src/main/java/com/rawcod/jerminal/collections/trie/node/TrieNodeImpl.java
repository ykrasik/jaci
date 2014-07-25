package com.rawcod.jerminal.collections.trie.node;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class TrieNodeImpl<T> implements TrieNode<T> {
    private final Map<Character, TrieNode<T>> children;
    private final TrieNode<T> parent;
    private final char character;

    private T value;

    public TrieNodeImpl() {
        this.children = new HashMap<>(1);
        this.parent = null;
        this.character = (char) 0;
    }

    public TrieNodeImpl(char character, TrieNode<T> parent) {
        this.children = new HashMap<>(1);
        this.parent = parent;
        this.character = character;
    }

    @Override
    public int numChildren() {
        return children.size();
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public char getCharacter() {
        return character;
    }

    @Override
    public String getPrefix() {
        if (parent == null) {
            return "";
        } else {
            return String.format("%s%c", parent.toString(), character);
        }
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
    public T setValue(T value) {
        this.value = value;
    }

    @Override
    public TrieNode<T> getChild(char c) {
        TrieNode<T> child = children.get(Character.toLowerCase(c));
        if (child == null) {
            child = children.get(Character.toUpperCase(c));
        }
        return child;
    }

    @Override
    public void setChild(char c, TrieNode<T> child) {
        checkArgument(getChild(c) == null, "Node already has a child at '%c'", c);
        children.put(c, child);
    }

    @Override
    public Iterable<TrieNode<T>> getChildren() {
        return children.values();
    }

    @Override
    public String toString() {
        return getPrefix();
    }
}
