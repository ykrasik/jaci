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

package com.github.ykrasik.jaci.util.trie;

import com.github.ykrasik.jaci.util.function.Func;
import com.github.ykrasik.jaci.util.function.Pred;
import com.github.ykrasik.jaci.util.opt.Opt;

import java.util.*;
import java.util.Map.Entry;

/**
 * An implementation of a {@link Trie}.
 * Contains a character, a possible value, and children.
 *
 * @author Yevgeny Krasik
 */
// FIXME: Test this class thoroughly!
public class TrieNode<T> implements Trie<T> {
    private final char c;
    private final Map<Character, TrieNode<T>> children;

    private Opt<T> value = Opt.absent();

    private volatile boolean numWordsCalculated;
    private int numWords;

    /**
     * Create a new node with the given character and no children.
     *
     * @param c Character to assign to this node.
     */
    public TrieNode(char c) {
        this(c, new HashMap<Character, TrieNode<T>>(1));
    }

    private TrieNode(char c, Map<Character, TrieNode<T>> children) {
        this.c = c;
        this.children = children;
    }

    @Override
    public int size() {
        if (!numWordsCalculated) {
            // TODO: Am I overdoing this? Thread safety? For what?
            synchronized (this) {
                calcNumWords();
            }
        }
        return numWords;
    }

    /**
     * The amount of words reachable from this node is cached.<br>
     * This should be called after an outside changed had occurred and the amount was possibly changed.
     */
    private void calcNumWords() {
        if (numWordsCalculated) {
            return;
        }

        int words = 0;

        // The number of words reachable from this node is considered to be the number of words
        // reachable from it's children, and +1 if the node itself is a word.
        if (value.isPresent()) {
            words++;
        }

        for (TrieNode<T> child : children.values()) {
            child.calcNumWords();
            words += child.numWords;
        }

        numWords = words;
        numWordsCalculated = true;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(String word) {
        final Opt<TrieNode<T>> node = getNode(word);
        return node.exists(IS_WORD_PREDICATE);
    }

    @Override
    public Opt<T> get(String word) {
        final Opt<TrieNode<T>> node = getNode(word);
        if (!node.isPresent()) {
            return Opt.absent();
        }
        return node.get().value;
    }

    @Override
    public String getLongestPrefix() {
        if (isEmpty()) {
            return "";
        }

        // Keep going down the tree, until a node has more than 1 children or is a word.
        final StringBuilder prefixBuilder = new StringBuilder();
        TrieNode<T> currentNode = this;
        while (currentNode.children.size() == 1 && !currentNode.isWord()) {
            // currentNode only has 1 child and is not a word.
            // Move on to currentNode's only child.
            currentNode = currentNode.children.values().iterator().next();

            // Append child's character to prefix.
            prefixBuilder.append(currentNode.c);
        }
        return prefixBuilder.toString();
    }

    @Override
    public Trie<T> subTrie(String prefix) {
        if (prefix.isEmpty() || this.isEmpty()) {
            return this;
        }

        // currentNavigationNode is used to navigate down the existing trie.
        // currentCreationNode is used to create a new trie according to the prefix.
        final TrieNode<T> prefixTrie = createRoot();
        TrieNode<T> currentNavigationNode = this;
        TrieNode<T> currentCreationNode = prefixTrie;

        // TODO: Consider a more elegant solution.
        for (int i = 0; i < prefix.length(); i++) {
            final char c = prefix.charAt(i);
            final Opt<TrieNode<T>> child = currentNavigationNode.getChild(c);
            if (!child.isPresent()) {
                return emptyTrie();
            }
            currentNavigationNode = child.get();

            final TrieNode<T> newChild;
            if (i == prefix.length() - 1) {
                // Use the real node for the last character in the word.
                newChild = currentNavigationNode;
            } else {
                // Create a new node for every character except the last, containing only 'c' as a child.
                newChild = new TrieNode<>(c);
            }
            currentCreationNode.children.put(c, newChild);
            currentCreationNode = newChild;
        }

        return prefixTrie;
    }

    @Override
    public <A> TrieNode<A> mapValues(Func<T, A> function) {
        if (isEmpty()) {
            return emptyTrie();
        }

        final Opt<TrieNode<A>> newTrie = this.doMap(function);
        return newTrie.getOrElse(TrieNode.<A>emptyTrie());
    }

    private <A> Opt<TrieNode<A>> doMap(Func<T, A> function) {
        final Opt<A> newValue = value.map(function);

        // Map the node's children.
        final Map<Character, TrieNode<A>> newChildren = mapChildren(function);
        if (newChildren.isEmpty() && !newValue.isPresent()) {
            // The node didn't map to a value and neither did it's children.
            return Opt.absent();
        }

        // Create a new node.
        final TrieNode<A> newNode = new TrieNode<>(c, newChildren);
        newNode.value = newValue;
        return Opt.of(newNode);
    }

    private <A> Map<Character, TrieNode<A>> mapChildren(Func<T, A> function) {
        if (children.isEmpty()) {
            return Collections.emptyMap();
        }

        final Map<Character, TrieNode<A>> newChildren = new HashMap<>(children.size());
        for (TrieNode<T> child : children.values()) {
            final Opt<TrieNode<A>> newChild = child.doMap(function);
            if (newChild.isPresent()) {
                newChildren.put(child.c, newChild.get());
            }
        }
        return newChildren;
    }

    @Override
    public Trie<T> filter(final Pred<T> filter) {
        return mapValues(new Func<T, T>() {
            @Override
            public T apply(T input) {
                return filter.test(input) ? input : null;
            }
        });
    }

    @Override
    public Trie<T> union(Trie<T> other) {
        if (this == other || this.isEmpty()) {
            return other;
        }
        if (other.isEmpty()) {
            return this;
        }

        if (other instanceof TrieNode) {
            // Other Trie is of the same implementation, we can have an efficient union.
            return trieNodeUnion((TrieNode<T>) other);
        }

        // Other Trie is of a different implementation, create a naive union trie.
        final TrieBuilder<T> builder = new TrieBuilder<>();
        builder.setAll(this.toMap());
        builder.setAll(other.toMap());
        return builder.build();
    }

    private TrieNode<T> trieNodeUnion(TrieNode<T> other) {
        final char otherCharacter = other.c;
        if (Character.toLowerCase(c) != Character.toLowerCase(otherCharacter)) {
            // TODO: Is this the correct way of handling this?
            throw new IllegalArgumentException("Trying to create a union between incompatible nodes: " + c + " and " + otherCharacter);
        }

        // Check which of this node's children are also present in other and vice versa.
        // Those that are unique will be used as is.
        // Those that are present in both will be replaced with a UnionNode.
        final Map<Character, TrieNode<T>> unionChildren = new HashMap<>(children.size() + other.children.size());
        this.mergeChildren(other, unionChildren);
        other.mergeChildren(this, unionChildren);

        final TrieNode<T> unionNode = new TrieNode<>(c, unionChildren);
        unionNode.value = value.orElse(other.value);
        return unionNode;
    }

    private void mergeChildren(TrieNode<T> otherNode, Map<Character, TrieNode<T>> unionChildren) {
        for (TrieNode<T> child : children.values()) {
            final char childCharacter = child.c;
            if (unionChildren.containsKey(childCharacter)) {
                // This node's character was already handled in a previous iteration.
                continue;
            }

            final Opt<TrieNode<T>> otherChild = otherNode.getChild(childCharacter);
            final TrieNode<T> trieNodeToAdd;
            if (otherChild.isPresent()) {
                // The other node has a child under 'c', use a union node.
                trieNodeToAdd = child.trieNodeUnion(otherChild.get());
            } else {
                // The other node has no child under 'c', can use the original node.
                trieNodeToAdd = child;
            }
            unionChildren.put(childCharacter, trieNodeToAdd);
        }
    }

    @Override
    public void visitWords(TrieVisitor<T> visitor) {
        if (isEmpty()) {
            return;
        }

        // doVisit does not push or pop characters from the wordBuilder.
        // We call it (and not visit()) because we don't want the root's character to be appended.
        doVisit(visitor, new StringBuilder());
    }

    private void visit(TrieVisitor<T> visitor, StringBuilder wordBuilder) {
        // Started processing node, push it's character to the prefix.
        wordBuilder.append(c);

        // Actually visit the node.
        doVisit(visitor, wordBuilder);

        // Done processing node, pop it's character from the prefix.
        wordBuilder.deleteCharAt(wordBuilder.length() - 1);
    }

    private void doVisit(TrieVisitor<T> visitor, StringBuilder wordBuilder) {
        // Visit the node if it has a value.
        if (value.isPresent()) {
            final String word = wordBuilder.toString();
            visitor.visit(word, value.get());
        }

        // Visit all the node's children.
        for (TrieNode<T> child : children.values()) {
            child.visit(visitor, wordBuilder);
        }
    }

    @Override
    public Collection<String> words() {
        return toMap().keySet();
    }

    @Override
    public Collection<T> values() {
        return toMap().values();
    }

    @Override
    public Set<Entry<String, T>> entrySet() {
        return toMap().entrySet();
    }

    @Override
    public Map<String, T> toMap() {
        if (isEmpty()) {
            return Collections.emptyMap();
        }

        final MapTrieVisitor<T> visitor = new MapTrieVisitor<>();
        visitWords(visitor);
        return visitor.getMap();
    }

    private boolean isWord() {
        return value.isPresent();
    }

    private Opt<TrieNode<T>> getNode(String prefix) {
        // Navigate the tree by the letters of the prefix, starting from the root.
        TrieNode<T> currentNode = this;
        for (int i = 0; i < prefix.length(); i++) {
            final char c = prefix.charAt(i);
            final Opt<TrieNode<T>> child = currentNode.getChild(c);
            if (!child.isPresent()) {
                return Opt.absent();
            }
            currentNode = child.get();
        }
        return Opt.of(currentNode);
    }

    /**
     * @return Child node for character 'c', if one exists. <b>Case insensitive</b>
     */
    Opt<TrieNode<T>> getChild(char c) {
        if (children.isEmpty()) {
            return Opt.absent();
        }

        TrieNode<T> child = children.get(Character.toLowerCase(c));
        if (child == null) {
            child = children.get(Character.toUpperCase(c));
        }
        return Opt.ofNullable(child);
    }

    /**
     * Sets the given node as a child of this node. Will overwrite any existing child for the child's character.
     *
     * @param child TrieNode to set as a child of this node.
     */
    void setChild(TrieNode<T> child) {
        children.put(child.c, child);
    }

    /**
     * Sets the value of this node.
     *
     * @param value The value to set. May be null.
     */
    void setValue(T value) {
        this.value = Opt.ofNullable(value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (Character.isAlphabetic(c)) {
            sb.append(c);
            sb.append(" : ");
        }
        sb.append(toMap().toString());
        return sb.toString();
    }

    private static final TrieNode<?> EMPTY_TRIE = createRoot();

    /**
     * Create a node representing the root node of a Trie.
     *
     * @param <T> Trie type.
     * @return A root node of a Trie.
     */
    public static <T> TrieNode<T> createRoot() {
        return new TrieNode<>((char) 0);
    }

    /**
     * @param <T> Trie type.
     * @return An empty Trie.
     */
    @SuppressWarnings("unchecked")
    public static <T> TrieNode<T> emptyTrie() {
        return (TrieNode<T>) EMPTY_TRIE;
    }

    private static final Pred<TrieNode<?>> IS_WORD_PREDICATE = new Pred<TrieNode<?>>() {
        @Override
        public boolean test(TrieNode<?> trieNode) {
            return trieNode.isWord();
        }
    };
}