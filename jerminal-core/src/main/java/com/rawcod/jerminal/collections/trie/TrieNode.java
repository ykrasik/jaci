package com.rawcod.jerminal.collections.trie;

import com.rawcod.jerminal.collections.trie.TrieFilter.NoTrieFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrieNode<V> {
    private static final TrieFilter NO_FILTER = new NoTrieFilter<>();

    private final Map<Character, TrieNode<V>> children;
    private final TrieNode<V> parent;
    private final char character;     //The character this node represents

    private boolean isWord;     //Does this node represent the last character of a word
    private V value;

    public TrieNode() {
        this.children = new HashMap<>(1);
        this.parent = null;
        this.character = (char)0;
    }

    public TrieNode(char character, TrieNode<V> parent) {
        this.children = new HashMap<>(1);
        this.parent = parent;
        this.character = character;
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public boolean isWord() {
        return isWord;
    }

    public V getValue() {
        return value;
    }

    public void addWord(String word, V value) {
        final char c = word.charAt(0);
        TrieNode<V> child = getNode(c);
        if (child == null) {
            child = new TrieNode<>(c, this);
            children.put(c, child);
        }

        if (word.length() > 1) {
            child.addWord(word.substring(1), value);
        } else {
            if (child.value != null) {
                throw new RuntimeException("Child already has a value: " + child);
            }
            child.isWord = true;
            child.value = value;
        }
    }

    public TrieNode<V> getNode(char c) {
        TrieNode<V> child = children.get(Character.toLowerCase(c));
        if (child == null) {
            child = children.get(Character.toUpperCase(c));
        }
        return child;
    }

    public List<String> getAllWords() {
        return getWordsByFilter(NO_FILTER);
    }

    public List<String> getWordsByFilter(TrieFilter<V> filter) {
        final List<String> words = new ArrayList<>();

        if (isWord && !filter.shouldFilter(value)) {
            words.add(toString());
        }

        for (TrieNode<V> child : children.values()) {
            words.addAll(child.getWordsByFilter(filter));
        }

        return words;
    }

    public List<V> getAllValues() {
        return getValuesByFilter(NO_FILTER);
    }

    public List<V> getValuesByFilter(TrieFilter<V> filter) {
        final List<V> values = new ArrayList<>();

        if (isWord && !filter.shouldFilter(value)) {
            values.add(value);
        }

        for (TrieNode<V> child : children.values()) {
            values.addAll(child.getValuesByFilter(filter));
        }

        return values;
    }

    public String getLongestPrefix() {
        if (children.size() != 1 || isWord) {
            return toString();
        }

        // There is only 1 child
        for (TrieNode<V> child : children.values()) {
            return child.getLongestPrefix();
        }

        // Never reached
        return null;
    }

    public void visitWords(TrieVisitor<V> visitor) {
        if (isWord) {
            visitor.visit(toString(), value);
        }

        for (TrieNode<V> child : children.values()) {
            child.visitWords(visitor);
        }
    }

    /**
     * Gets the String that this node represents.
     * For example, if this node represents the character t, whose parent
     * represents the charater a, whose parent represents the character
     * c, then the String would be "cat".
     */
    public String toString() {
        if (parent == null) {
            return "";
        } else {
            return String.format("%s%c", parent.toString(), character);
        }
    }
}
