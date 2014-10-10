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

import com.google.common.base.Function;
import com.google.common.base.Optional;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation for a {@link TrieNode}.
 *
 * @author Yevgeny Krasik
 */
// FIXME: Test this class thoroughly!
public class TrieNodeImpl<T> implements TrieNode<T> {
    private final char c;
    private final Optional<T> value;
    private final Map<Character, TrieNode<T>> children;
    private final int numWords;

    public TrieNodeImpl(char c) {
        this(c, Optional.<T>absent());
    }

    public TrieNodeImpl(char c, T value) {
        this(c, Optional.of(value));
    }

    private TrieNodeImpl(char c, Optional<T> value) {
        this(c, value, Collections.<Character, TrieNode<T>>emptyMap());
    }

    private TrieNodeImpl(char c, Optional<T> value, Map<Character, TrieNode<T>> children) {
        this.c = c;
        this.value = value;
        this.children = children;
        this.numWords = calcNumWords();
    }

    private int calcNumWords() {
        // The number of words reachable from this node is considered to be the number of words
        // reachable from it's children, and +1 if the node itself is a word.
        int words = 0;
        if (value.isPresent()) {
            words++;
        }

        for (TrieNode<T> child : children.values()) {
            words += child.getNumWords();
        }

        return words;
    }

    @Override
    public boolean isWord() {
        return value.isPresent();
    }

    @Override
    public int getNumWords() {
        return numWords;
    }

    @Override
    public boolean isEmpty() {
        return numWords == 0;
    }

    @Override
    public char getCharacter() {
        return c;
    }

    @Override
    public Optional<T> getValue() {
        return value;
    }

    @Override
    public TrieNode<T> setValue(T value) {
        return new TrieNodeImpl<>(c, Optional.fromNullable(value), new HashMap<>(children));
    }

    @Override
    public Optional<TrieNode<T>> getChild(char c) {
        if (children.isEmpty()) {
            return Optional.absent();
        }

        TrieNode<T> child = children.get(Character.toLowerCase(c));
        if (child == null) {
            child = children.get(Character.toUpperCase(c));
        }
        return Optional.fromNullable(child);
    }

    @Override
    public TrieNode<T> setChild(TrieNode<T> child) {
        final Map<Character, TrieNode<T>> newChildren = new HashMap<>(children);
        newChildren.put(child.getCharacter(), child);
        return new TrieNodeImpl<>(c, value, newChildren);
    }

    @Override
    public Collection<TrieNode<T>> getChildren() {
        return Collections.unmodifiableCollection(children.values());
    }

    @Override
    public <A> Optional<TrieNode<A>> map(Function<T, A> function) {
        final Optional<A> newValue;
        if (value.isPresent()) {
            newValue = Optional.fromNullable(function.apply(value.get()));
        } else {
            newValue = Optional.absent();
        }

        // Map the node's children.
        final Map<Character, TrieNode<A>> newChildren = mapChildren(function);
        if (newChildren.isEmpty() && !newValue.isPresent()) {
            // The node didn't map to a value and neither did it's children.
            return Optional.absent();
        }

        // Create a new node.
        final TrieNode<A> newNode = new TrieNodeImpl<>(c, newValue, newChildren);
        return Optional.of(newNode);
    }

    private <A> Map<Character, TrieNode<A>> mapChildren(Function<T, A> function) {
        if (children.isEmpty()) {
            return Collections.emptyMap();
        }

        final Map<Character, TrieNode<A>> newChildren = new HashMap<>(children.size());
        for (TrieNode<T> child : children.values()) {
            final Optional<TrieNode<A>> newChild = child.map(function);
            if (newChild.isPresent()) {
                newChildren.put(child.getCharacter(), newChild.get());
            }
        }
        return newChildren;
    }

    @Override
    public TrieNode<T> union(TrieNode<T> other) {
        final char otherCharacter = other.getCharacter();
        if (c != otherCharacter) {
            throw new IllegalArgumentException("Trying to create a union between incompatible nodes: " + c + " and " + otherCharacter);
        }

        final Optional<T> unionValue = value.or(other.getValue());
        final Map<Character, TrieNode<T>> unionChildren = createUnionChildren(other);
        return new TrieNodeImpl<>(c, unionValue, unionChildren);
    }

    private Map<Character, TrieNode<T>> createUnionChildren(TrieNode<T> other) {
        // Check which of this node's children are also present in other and vice versa.
        // Those that are unique will be used as is.
        // Those that are present in both will be replaced with a UnionNode.
        final Map<Character, TrieNode<T>> unionChildren = new HashMap<>(children.size() + other.getChildren().size());
        checkNode(this, other, unionChildren);
        checkNode(other, this, unionChildren);
        return unionChildren;
    }

    private void checkNode(TrieNode<T> mainNode,
                           TrieNode<T> otherNode,
                           Map<Character, TrieNode<T>> unionChildren) {
        for (TrieNode<T> mainChild : mainNode.getChildren()) {
            final char childCharacter = mainChild.getCharacter();
            if (unionChildren.containsKey(childCharacter)) {
                // This node's character was already handled in a previous iteration.
                continue;
            }

            final Optional<TrieNode<T>> otherChild = otherNode.getChild(childCharacter);
            final TrieNode<T> trieNodeToAdd;
            if (otherChild.isPresent()) {
                // The other node has a child under 'c', use a union node.
                trieNodeToAdd = mainChild.union(otherChild.get());
            } else {
                // The other node has no child under 'c'.
                trieNodeToAdd = mainChild;
            }
            unionChildren.put(childCharacter, trieNodeToAdd);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(c);
    }
}
