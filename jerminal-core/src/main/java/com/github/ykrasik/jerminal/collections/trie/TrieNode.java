/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jerminal.collections.trie;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A node in a {@link Trie}. Contains a character, a possible value, and children.
 *
 * @author Yevgeny Krasik
 */
// FIXME: Test this class thoroughly!
public class TrieNode<T> {
    private final char c;
    private Optional<T> value;
    private final Map<Character, TrieNode<T>> children;
    private int numWords;

    public TrieNode(char c) {
        this(c, Optional.<T>absent());
    }

    public TrieNode(char c, T value) {
        this(c, Optional.of(value));
    }

    private TrieNode(char c, Optional<T> value) {
        this(c, value, new HashMap<Character, TrieNode<T>>(0));
    }

    private TrieNode(char c, Optional<T> value, Map<Character, TrieNode<T>> children) {
        this.c = c;
        this.value = value;
        this.children = children;
    }

    /**
     * @return True if this node represents a word.<br>
     *         If true, {@link #getValue()} must return a present value.
     */
    public boolean isWord() {
        return value.isPresent();
    }

    /**
     * @return The number of words that are reachable from this node.
     */
    public int getNumWords() {
        return numWords;
    }

    /**
     * The amount of words reachable from this node is cached.<br>
     * This should be called after an outside changed had occurred and the amount was possibly changed.
     */
    public void refershNumWords() {
        // First, refresh the number of words in the children.
        for (TrieNode<T> child : children.values()) {
            child.refershNumWords();
        }

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

    /**
     * @return True if this node is not a word and does not have any children.
     */
    public boolean isEmpty() {
        return numWords == 0;
    }

    /**
     * @return The character associated with this node.
     */
    public char getCharacter() {
        return c;
    }

    /**
     * @return The value contained in this node.
     */
    public Optional<T> getValue() {
        return value;
    }

    /**
     * Sets the value of this node.
     *
     * @param value The value to set. may be null.
     */
    public void setValue(T value) {
        this.value = Optional.fromNullable(value);
    }

    /**
     * @param c Character to get child node for.
     * @return A child node associated with the character.<br>
     *         <b>Case insensitive</b>
     */
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

    /**
     * @param c Character to get or create child node for.
     * @return An existing child node for the given character if one existed, or a new one otherwise.
     */
    public TrieNode<T> getOrCreateChild(char c) {
        final Optional<TrieNode<T>> child = getChild(c);
        if (child.isPresent()) {
            return child.get();
        }

        final TrieNode<T> newChild = new TrieNode<>(c);
        children.put(c, newChild);
        return newChild;
    }

    /**
     * @return A collection of all this node's children.
     */
    public Collection<TrieNode<T>> getChildren() {
        return Collections.unmodifiableCollection(children.values());
    }

    /**
     * @param function Function to apply to the value and every child node's value.
     * @param <A> Type to transform values to.
     * @return A copy of this node in which the value (if present) and all the node's children are transformed by
     *         calling {@link Function#apply(T)}.<br>
     *         If the result of the transformation returns 'null' for the node's value and for all it's children,
     *         that node will not be present in the return value.<br>
     *         Does not alter this node.
     */
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
        final TrieNode<A> newNode = new TrieNode<>(c, newValue, newChildren);
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

    /**
     * @param other The other node of the union.
     * @return A copy of this node that is a union with the other node. The {@link #getValue()} of the returned node is either
     *         this node's value or the other node's value, without guarantees. The node's children also
     *         undergo this union process.<br>
     *         Does not alter this node.
     * @throws java.lang.IllegalArgumentException If the {@link #getCharacter()} of this node and the other node are different.
     */
    public TrieNode<T> union(TrieNode<T> other) {
        final char otherCharacter = other.getCharacter();
        if (c != otherCharacter) {
            throw new IllegalArgumentException("Trying to create a union between incompatible nodes: " + c + " and " + otherCharacter);
        }

        final Optional<T> unionValue = value.or(other.getValue());
        final Map<Character, TrieNode<T>> unionChildren = createUnionChildren(other);
        return new TrieNode<>(c, unionValue, unionChildren);
    }

    private Map<Character, TrieNode<T>> createUnionChildren(TrieNode<T> other) {
        // Check which of this node's children are also present in other and vice versa.
        // Those that are unique will be used as is.
        // Those that are present in both will be replaced with a UnionNode.
        final Map<Character, TrieNode<T>> unionChildren = new HashMap<>(children.size() + other.getChildren().size());
        mergeChildren(this, other, unionChildren);
        mergeChildren(other, this, unionChildren);
        return unionChildren;
    }

    private void mergeChildren(TrieNode<T> mainNode, TrieNode<T> otherNode, Map<Character, TrieNode<T>> unionChildren) {
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
