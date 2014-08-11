package com.rawcod.jerminal.collections.trie;

import com.rawcod.jerminal.collections.trie.node.TrieNode;
import com.rawcod.jerminal.collections.trie.node.TrieNodeBuilder;
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
    private final TrieNodeBuilder<T> rootBuilder;

    public TrieBuilder() {
        this.values = new HashMap<>();
        this.rootBuilder = new TrieNodeBuilder<>();
    }

    public Trie<T> build() {
        final TrieNode<T> root = rootBuilder.build();
        return new TrieImpl<>(root);
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
        putWord(word, value);
        return this;
    }

    private void putWord(String word, T value) {
        // Navigate the tree by the letters of the word, starting from the root.
        TrieNodeBuilder<T> currentNode = rootBuilder;
        for (int i = 0; i < word.length(); i++) {
            final char c = word.charAt(i);
            currentNode = rootBuilder.getOrCreateNode(c);
        }

        currentNode.setValue(value);
    }
}
