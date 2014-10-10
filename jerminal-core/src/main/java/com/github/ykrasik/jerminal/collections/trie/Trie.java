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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An <b>immutable</b> prefix tree.<br>
 *
 * @author Yevgeny Krasik
 */
public interface Trie<T> {
    /**
     * @return The amount of words in this Trie.
     */
    int size();

    /**
     * @return True if this Trie does not contain any words.
     */
    boolean isEmpty();

    /**
     * @return A Trie with the given word-value mapping added.<br>
     *         Does not alter this Trie.
     * @throws java.lang.IllegalStateException If this Trie already contained a mapping for the given word.
     */
    Trie<T> add(String word, T value);

    /**
     * @return A Trie with the given word-value mapping set. Overwrites any previous mapping.<br>
     *         Does not alter this Trie.
     */
    Trie<T> set(String word, T value);

    /**
     * @return The root {@link TrieNode}. {@link TrieNode}s are <b>immutable</b>.
     */
    // FIXME: This is only needed for union, make sure this should be exposed.
    TrieNode<T> getRoot();

    /**
     * @return True if this Trie contains the word.<br>
     */
    boolean contains(String word);

    /**
     * @return The value associated with the word.
     */
    Optional<T> get(String word);

    /**
     * @return All the words in this Trie.
     */
    Collection<String> getWords();

    /**
     * @return All the values in this Trie.
     */
    Collection<T> getValues();

    /**
     * Calls {@link TrieVisitor#visit(String, Object)}
     * for each word-value pair in this Trie.
     */
    void visitWords(TrieVisitor<T> visitor);

    /**
     * @return The longest common prefix in this Trie.<br>
     *         For example, for the words 'abc', 'abcd' and 'abcde', the longest common prefix is 'abc'.
     */
    String getLongestPrefix();

    /**
     * @return A {@link Trie} with words that start with the prefix. <br>
     *         If no such words exist in the Trie, an empty Trie is returned.<br>
     *         Does not alter this Trie.
     */
    Trie<T> subTrie(String prefix);

    /**
     * @return A Trie in which the value of each word was transformed by calling {@link Function#apply(Object)}.<br>
     *         If the result of the transformation returned 'null', that word and value will not appear in the returned Trie.<br>
     *         Does not alter this Trie.
     */
    <A> Trie<A> map(Function<T, A> function);

    /**
     * @return A Trie in which all values for which {@link Predicate#apply(Object)} returned false are removed.<br>
     *         Does not alter this Trie.
     */
    Trie<T> filter(Predicate<T> filter);

    /**
     * @return A Trie which contains words and values from this Trie and the other Trie.<br>
     *         If a word was contained in both Tries, the returned Trie will map that word to either one
     *         of the possible values.<br>
     *         Does not alter this Trie.
     */
    Trie<T> union(Trie<T> other);

    /**
     * @return A {@link Set} containing all word-value pairs in this Trie.
     */
    Set<Entry<String, T>> entrySet();

    /**
     * Returns a {@link Map} containing all word-value pairs in this Trie.
     */
    Map<String, T> toMap();
}
