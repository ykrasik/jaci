/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.collections.trie;

import com.github.ykrasik.jerminal.collections.trie.node.TrieNode;
import com.github.ykrasik.jerminal.collections.trie.node.TrieNodeBuilder;
import com.github.ykrasik.jerminal.internal.exception.ShellException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A builder for a {@link Trie}.<br>
 * {@link Trie}s are <b>immutable</b> once built.
 *
 * @author Yevgeny Krasik
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
            currentNode = currentNode.getOrCreateNode(c);
        }

        currentNode.setValue(value);
    }
}
