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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * An implementation for a {@link TrieNode}. <b>Immutable</b>.
 *
 * @author Yevgeny Krasik
 */
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
