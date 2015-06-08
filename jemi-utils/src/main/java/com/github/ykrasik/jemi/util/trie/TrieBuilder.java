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

package com.github.ykrasik.jemi.util.trie;

import com.github.ykrasik.jemi.util.opt.Opt;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A builder for a {@link Trie}. A {@link Trie} cannot be modified once built.
 *
 * @author Yevgeny Krasik
 */
public class TrieBuilder<T> {
    private final Map<String, T> map = new HashMap<>();

    /**
     * Add a word-value mapping to the Trie. Expects there not to be a previous mapping for the word.
     *
     * @param word The word for the word-value mapping.
     * @param value The value for the word-value mapping.
     * @return {@code this}, for chaining.
     * @throws IllegalStateException If this Trie already contained a mapping for the given word.
     */
    public TrieBuilder<T> add(String word, T value) {
        assertNotEmptyWord(word);

        // Save the word-value pair in the map. The actual construction will be done later.
        final T prevValue = map.put(word, value);
        if (prevValue != null) {
            throw new IllegalArgumentException(String.format("Trie already contains a value for '%s': %s", word, prevValue));
        }
        return this;
    }

    /**
     * Add all word-value pairs from the given map to the Trie.
     * Expects there not to be any previous mappings for any of the words.
     *
     * @param map Map to add word-value pairs from.
     * @return {@code this}, for chaining.
     * @throws IllegalStateException If this Trie already contained a mapping for any of the map's words (keys).
     */
    public TrieBuilder<T> addAll(Map<String, T> map) {
        for (Entry<String, T> entry : map.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Set a word-value mapping on the Trie. If a previous mapping exists, it will be overwritten.
     * Otherwise, will create a new word-value mapping.
     *
     * @param word The word for the word-value mapping.
     * @param value The value for the word-value mapping.
     * @return {@code this}, for chaining.
     */
    public TrieBuilder<T> set(String word, T value) {
        assertNotEmptyWord(word);

        // Save the word-value pair in the map. The actual construction will be done later.
        map.put(word, value);
        return this;
    }

    /**
     * Set all word-value pairs in the given map to the Trie.
     * If any previous mapping exists, it will be overwritten.
     *
     * @param map Map to set word-value pairs from.
     * @return {@code this}, for chaining.
     */
    public TrieBuilder<T> setAll(Map<String, T> map) {
        for (Entry<String, T> entry : map.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * @return A {@link Trie} created from the word-value mappings in this {@link TrieBuilder}.
     */
    public Trie<T> build() {
        final TrieNode<T> root = TrieNode.createRoot();
        for (Entry<String, T> entry : map.entrySet()) {
            createTrieBranch(root, entry.getKey(), entry.getValue());
        }
        return root;
    }

    private void createTrieBranch(TrieNode<T> root, String word, T value) {
        TrieNode<T> currentNode = root;
        for (int i = 0; i < word.length(); i++) {
            final char c = word.charAt(i);
            final Opt<TrieNode<T>> child = currentNode.getChild(c);
            if (child.isPresent()) {
                // currentNode already has a child node for 'c'.
                currentNode = child.get();
            } else {
                // currentNode does not have a child node for 'c', create a new one.
                final TrieNode<T> newChild = new TrieNode<>(c);
                currentNode.setChild(newChild);
                currentNode = newChild;
            }
        }
        currentNode.setValue(value);
    }

    private void assertNotEmptyWord(String word) {
        if (word.isEmpty()) {
            throw new IllegalArgumentException("Empty words aren't allowed!");
        }
    }
}
