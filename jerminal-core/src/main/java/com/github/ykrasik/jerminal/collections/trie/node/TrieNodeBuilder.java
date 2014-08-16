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

package com.github.ykrasik.jerminal.collections.trie.node;

import com.github.ykrasik.jerminal.internal.exception.ShellException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A builder for a {@link TrieNode}.<br>
 * {@link TrieNode}s are <b>immutable</b> once built.
 *
 * @author Yevgeny Krasik
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
