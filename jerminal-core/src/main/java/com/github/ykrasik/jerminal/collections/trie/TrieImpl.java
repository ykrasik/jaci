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
import com.google.common.base.Predicate;

import java.util.*;
import java.util.Map.Entry;

/**
 * An implementation for a {@link Trie}.
 *
 * @author Yevgeny Krasik
 */
public class TrieImpl<T> implements Trie<T> {
    private final TrieNode<T> root;

    TrieImpl(TrieNode<T> root) {
        this.root = root;
        root.refreshNumWords();
    }

    @Override
    public int size() {
        return root.getNumWords();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(String word) {
        final TrieNode<T> node = getNode(word);
        return node != null && node.isWord();
    }

    @Override
    public Optional<T> get(String word) {
        final TrieNode<T> node = getNode(word);
        if (node != null) {
            return node.getValue();
        } else {
            return Optional.absent();
        }
    }

    @Override
    public String getLongestPrefix() {
        if (isEmpty()) {
            return "";
        }

        // Keep going down the tree, until a node has more than 1 children or is a word.
        final StringBuilder prefixBuilder = new StringBuilder();
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
        if (prefix.isEmpty() || this.isEmpty()) {
            return this;
        }

        // Save the new root node, will be required for later.
//        final Optional<TrieNode<T>> newRootOptional = root.subTrieNode(prefix.charAt(0));
//        if (!newRootOptional.isPresent()) {
//            return Tries.emptyTrie();
//        }
//        final TrieNode<T> newRoot = newRootOptional.get();
//
//        // newRoot is guaranteed to have a child under the first character of the prefix at this point.
//        TrieNode<T> currentNode = newRoot.getChild(prefix.charAt(0)).get();
//        for (int i = 1; i < prefix.length(); i++) {
//            final char c = prefix.charAt(i);
//            final Optional<TrieNode<T>> newNodeOptional = currentNode.subTrieNode(c);
//            if (!newNodeOptional.isPresent()) {
//                return Tries.emptyTrie();
//            }
//
//            final TrieNode<T> newNode = newNodeOptional.get();
//            currentNode.setChild(newNode);
//            currentNode = newNode.getChild(c).get();
//        }
        final Optional<TrieNode<T>> newRoot = root.subTrie(prefix);
        if (newRoot.isPresent()) {
            return new TrieImpl<>(newRoot.get());
        } else {
            return Tries.emptyTrie();
        }
    }

    @Override
    public <A> Trie<A> map(Function<T, A> function) {
        if (isEmpty()) {
            return Tries.emptyTrie();
        }

        final Optional<TrieNode<A>> newRoot = root.map(function);
        if (newRoot.isPresent()) {
            return new TrieImpl<>(newRoot.get());
        } else {
            return Tries.emptyTrie();
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

        if (other instanceof TrieImpl) {
            // Other Trie is of the same implementation, we can have an efficient union.
            final TrieNode<T> unionRoot = root.union(((TrieImpl<T>) other).root);
            return new TrieImpl<>(unionRoot);
        }

        // Other Trie is of a different implementation, create a naive union trie.
        final TrieBuilder<T> builder = new TrieBuilder<>();
        builder.setAll(this.toMap());
        builder.setAll(other.toMap());
        return builder.build();
    }

    @Override
    public void visitWords(TrieVisitor<T> visitor) {
        if (isEmpty()) {
            return;
        }

        visitWordsFromNode(root, visitor, new StringBuilder());
    }

    private void visitWordsFromNode(TrieNode<T> node, TrieVisitor<T> visitor, StringBuilder prefixBuilder) {
        // Started processing node, push it's character to the prefix.
        if (node != root) {
            // The root node has no char.
            prefixBuilder.append(node.getCharacter());
        }

        // Visit the node, if it has a value.
        final Optional<T> value = node.getValue();
        if (value.isPresent()) {
            final String word = prefixBuilder.toString();
            visitor.visit(word, value.get());
        }

        // Visit all the node's children.
        for (TrieNode<T> child : node.getChildren()) {
            visitWordsFromNode(child, visitor, prefixBuilder);
        }

        // Done processing node, pop it's character from the prefix.
        if (node != root) {
            prefixBuilder.deleteCharAt(prefixBuilder.length() - 1);
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

    @Override
    public String toString() {
        return toMap().toString();
    }
}
