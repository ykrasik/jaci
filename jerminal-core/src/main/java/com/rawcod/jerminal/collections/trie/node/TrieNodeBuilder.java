package com.rawcod.jerminal.collections.trie.node;

import com.rawcod.jerminal.exception.ShellException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: ykrasik
 * Date: 11/08/2014
 * Time: 00:30
 */
public class TrieNodeBuilder<T> {
    private final char character;
    private final Map<Character, TrieNodeBuilder<T>> childBuilders;

    private T value;

    public TrieNodeBuilder() {
        this((char) 0);
    }

    private TrieNodeBuilder(char character) {
        this.character = character;
        this.childBuilders = new HashMap<>();
    }

    public TrieNode<T> build() {
        final Map<Character, TrieNode<T>> children = buildChildren();
        return new TrieNodeImpl<>(character, value, children);
    }

    private Map<Character, TrieNode<T>> buildChildren() {
        final Map<Character, TrieNode<T>> children = new HashMap<>(childBuilders.size());
        for (Entry<Character, TrieNodeBuilder<T>> entry : childBuilders.entrySet()) {
            final TrieNode<T> child = entry.getValue().build();
            children.put(entry.getKey(), child);
        }
        return children;
    }

    public void setValue(T value) {
        checkNotNull(value, "value");
        this.value = value;
    }

    public TrieNodeBuilder<T> getOrCreateNode(char c) {
        TrieNodeBuilder<T> childBuilder = TrieNodeUtils.getCaseInsensitive(childBuilders, c);
        if (childBuilder == null) {
            childBuilder = new TrieNodeBuilder<>(c);
            childBuilders.put(c, childBuilder);
        }
        return childBuilder;
    }

    public TrieNodeBuilder<T> addAll(Map<Character, T> map) {
        for (Entry<Character, T> entry : map.entrySet()) {
            final Character characater = entry.getKey();
            if (childBuilders.containsKey(characater)) {
                throw new ShellException("TrieNodeBuilder already contains a child for '%s'!", characater);
            }

            final TrieNodeBuilder<T> childBuilder = new TrieNodeBuilder<>(character);
            childBuilder.setValue(entry.getValue());
            childBuilders.put(characater, childBuilder);
        }
        return this;
    }
}
