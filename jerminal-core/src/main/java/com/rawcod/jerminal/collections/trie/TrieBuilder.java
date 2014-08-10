package com.rawcod.jerminal.collections.trie;

import com.rawcod.jerminal.collections.trie.node.TrieNodeImpl;
import com.rawcod.jerminal.collections.trie.node.ValueTrieNode;
import com.rawcod.jerminal.exception.ShellException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 10/08/2014
 * Time: 22:18
 */
public class TrieBuilder<T> {
    private final Map<String, T> values;
    private final ValueTrieNode<T> root;

    public TrieBuilder() {
        this.values = new HashMap<>();
        this.root = new TrieNodeImpl<>();
    }

    public Trie<T> build() {
        for (Entry<String, T> entry : values.entrySet()) {
            putWord(entry.getKey(), entry.getValue());
        }
        return new TrieImpl<>(root);
    }

    private void putWord(String word, T value) {
        // Navigate the tree by the letters of the word, starting from the root.
        ValueTrieNode<T> currentNode = root;
        for (int i = 0; i < word.length(); i++) {
            final char c = word.charAt(i);
            // FIXME: Create an immutable TrieNode
            ValueTrieNode<T> child = (ValueTrieNode<T>) currentNode.getChild(c);
            if (child == null) {
                child = new TrieNodeImpl<>(c);
                currentNode.setChild(c, child);
            }
            currentNode = child;
        }

        currentNode.setValue(value);
    }

    public TrieBuilder<T> addAll(Map<String, ? extends T> map) {
        for (Entry<String, ? extends T> entry : map.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public TrieBuilder<T> add(String word, T value) {
        checkArgument(!word.isEmpty(), "Empty words are not allowed!");
        checkNotNull(value, "Null values are not allowed!");

        final T prevValue = values.get(word);
        if (prevValue != null) {
            throw new ShellException("TrieBuilder already contains a value for '%s': %s", word, prevValue);
        }

        values.put(word, value);
        return this;
    }
}
