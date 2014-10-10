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
import com.google.common.base.Predicate;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An implementation for a {@link Trie}.
 *
 * @author Yevgeny Krasik
 */
public class TrieImpl<T> implements Trie<T> {
    private final TrieNode<T> root;

    // This is the prefix of the current trie. Used by subTries.
    private final String triePrefix;

    public TrieImpl() {
        this(new TrieNodeImpl<T>((char) 0));
    }

    private TrieImpl(TrieNode<T> root) {
        this(root, "");
    }

    private TrieImpl(TrieNode<T> root, String triePrefix) {
        this.root = root;
        this.triePrefix = triePrefix;
    }

    @Override
    public int size() {
        return root.getNumWords();
    }

    @Override
    public boolean isEmpty() {
        return root.isEmpty();
    }

    @Override
    public Trie<T> add(String word, T value) {
        if (word.isEmpty()) {
            throw new IllegalArgumentException("Empty words aren't allowed!");
        }

        final TrieNode<T> newRoot = doPut(root, word, 0, value, false);
        return new TrieImpl<>(newRoot, triePrefix);
    }

    @Override
    public Trie<T> set(String word, T value) {
        if (word.isEmpty()) {
            throw new IllegalArgumentException("Empty words aren't allowed!");
        }

        final TrieNode<T> newRoot = doPut(root, word, 0, value, true);
        return new TrieImpl<>(newRoot, triePrefix);
    }

    // FIXME: Doesn't work with subTries!
    // FIXME: Test this thoroughly!
    // FIXME: This method is pretty ugly...
    private TrieNode<T> doPut(TrieNode<T> parent, String word, int index, T value, boolean replace) {
        final char c = word.charAt(index);
        final Optional<TrieNode<T>> child = parent.getChild(c);
        final TrieNode<T> currentChild;
        if (child.isPresent()) {
            currentChild = child.get();
        } else {
            currentChild = new TrieNodeImpl<>(c);
        }

        final TrieNode<T> newChild;
        if (index == word.length() - 1) {
            // Last character of the word, set node.
            if (!replace && currentChild.getValue().isPresent()) {
                throw new IllegalArgumentException("Trie already contains a value for: " + word);
            }
            newChild = currentChild.setValue(value);
        } else {
            newChild = doPut(currentChild, word, index + 1, value, replace);
        }
        return parent.setChild(newChild);
    }

//    private TrieNode<T> doPut(TrieNode<T> parent, String word, int index, T value) {
//        final char c = word.charAt(index);
//
//        if (index == word.length() - 1) {
//            // Last character of the word, create a value node.
//            final Optional<TrieNode<T>> child = parent.getChild(c);
//            final TrieNode<T> newChild;
//            if (child.isPresent()) {
//                if (child.get().getValue().isPresent()) {
//                    throw new IllegalArgumentException("Trie already contains a value for: " + word);
//                }
//                newChild = child.get().setValue(value);
//            } else {
//                newChild = new TrieNodeImpl<>(c, value);
//            }
//            return parent.setChild(newChild);
//        }
//
//        final Optional<TrieNode<T>> child = parent.getChild(c);
//        final TrieNode<T> currentChild;
//        if (child.isPresent()) {
//            currentChild = child.get();
//        } else {
//            currentChild = new TrieNodeImpl<>(c);
//        }
//
//        final TrieNode<T> newChild = doPut(currentChild, word, index + 1, value);
//        return parent.setChild(newChild);
//    }

    @Override
    public TrieNode<T> getRoot() {
        return root;
    }

    @Override
    public boolean contains(String word) {
        final TrieNode<T> node = getNode(word);
        return node != null && node.isWord();
    }

    @Override
    // FIXME: This will not function correctly for subTries!
    public Optional<T> get(String word) {
        final TrieNode<T> node = getNode(word);
        if (node != null) {
            return node.getValue();
        } else {
            return Optional.absent();
        }
    }

    @Override
    public Collection<String> getWords() {
        if (isEmpty()) {
            return Collections.emptyList();
        }

        final MapTrieVisitor<T> visitor = new MapTrieVisitor<>();
        visitWords(visitor);
        return visitor.getMap().keySet();
    }

    @Override
    public Collection<T> getValues() {
        if (isEmpty()) {
            return Collections.emptyList();
        }

        final MapTrieVisitor<T> visitor = new MapTrieVisitor<>();
        visitWords(visitor);
        return visitor.getMap().values();
    }

    @Override
    public void visitWords(TrieVisitor<T> visitor) {
        if (isEmpty()) {
            return;
        }

        final StringBuilder prefixBuilder = new StringBuilder(triePrefix);
        visitWordsFromNode(root, visitor, prefixBuilder);
    }

    private void visitWordsFromNode(TrieNode<T> node, TrieVisitor<T> visitor, StringBuilder prefixBuilder) {
        // Started processing node, push it's character to the prefix.
        if (node != root) {
            // The root node has no char.
            prefixBuilder.append(node.getCharacter());
        }

        // Visit the node, if it has a value.
        visitNodeIfHasValue(node, visitor, prefixBuilder);

        // Visit all the node's children.
        for (TrieNode<T> child : node.getChildren()) {
            visitWordsFromNode(child, visitor, prefixBuilder);
        }

        // Done processing node, pop it's character from the prefix.
        if (node != root && prefixBuilder.length() > 0) {
            prefixBuilder.deleteCharAt(prefixBuilder.length() - 1);
        }
    }

    private void visitNodeIfHasValue(TrieNode<T> node, TrieVisitor<T> visitor, StringBuilder prefixBuilder) {
        final Optional<T> value = node.getValue();
        if (value.isPresent()) {
            final String word = prefixBuilder.toString();
            visitor.visit(word, value.get());
        }
    }

    @Override
    public String getLongestPrefix() {
        if (isEmpty()) {
            return "";
        }

        // Keep going down the tree, until a node has more than 1 children or is a word.
        final StringBuilder prefixBuilder = new StringBuilder(triePrefix);
        TrieNode<T> currentNode = root;
        while (currentNode.getChildren().size() == 1 && !currentNode.isWord()) {
            // currentNode only has 1 child and is not a word.
            for (TrieNode<T> child : currentNode.getChildren()) {
                // Move on to currentNode's only child.
                currentNode = child;

                // Append child's character to prefix.
                prefixBuilder.append(currentNode.getCharacter());
            }
        }

        return prefixBuilder.toString();
    }

    @Override
    // FIXME: This requires testing.
    public Trie<T> subTrie(String prefix) {
        if (isEmpty()) {
            return this;
        }

        final TrieNode<T> node = getNode(prefix);
        if (node != null) {
            return new TrieImpl<>(node, triePrefix + prefix);
        } else {
            return emptyTrie();
        }
    }

    @Override
    public <A> Trie<A> map(Function<T, A> function) {
        if (isEmpty()) {
            return emptyTrie();
        }

        final Optional<TrieNode<A>> newRoot = root.map(function);
        if (newRoot.isPresent()) {
            return new TrieImpl<>(newRoot.get(), triePrefix);
        } else {
            // Empty root.
            return emptyTrie();
        }
    }

    @Override
    public Trie<T> filter(final Predicate<T> filter) {
        return map(new Function<T, T>() {
            @Override
            public T apply(T input) {
                return filter.apply(input) ? input : null;
            }
        });
    }

    @Override
    public Trie<T> union(Trie<T> other) {
        if (isEmpty()) {
            return other;
        }
        if (other.isEmpty()) {
            return this;
        }

        final TrieNode<T> unionRoot = root.union(other.getRoot());
        return new TrieImpl<>(unionRoot, triePrefix);
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

    private TrieNode<T> getNode(String prefix) {
        // Navigate the tree by the letters of the prefix, starting from the root.
        TrieNode<T> currentNode = root;
        for (int i = 0; i < prefix.length(); i++) {
            final char c = prefix.charAt(i);
            final Optional<TrieNode<T>> child = currentNode.getChild(c);
            if (child.isPresent()) {
                currentNode = child.get();
            } else {
                return null;
            }
        }
        return currentNode;
    }

    /**
     * Returns an empty {@link Trie}.
     */
    @SuppressWarnings("unchecked")
    public static <T> Trie<T> emptyTrie() {
        return (Trie<T>) EMPTY_TRIE;
    }
    private static final Trie<?> EMPTY_TRIE = new TrieImpl<>();

    @Override
    public String toString() {
        return toMap().toString();
    }
}
