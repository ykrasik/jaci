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
import com.github.ykrasik.jerminal.collections.trie.node.TrieNode;
import com.github.ykrasik.jerminal.collections.trie.visitor.TrieVisitor;

import java.util.Collection;
import java.util.Map;

/**
 * An <b>immutable</b> prefix tree.<br>
 * Can be converted to a {@link Map} with {@link #toMap()} or from a {@link Map} with a {@link TrieBuilder}
 *
 * @author Yevgeny Krasik
 */
public interface Trie<T> {
    /**
     * Returns 'true' if this {@link Trie} does not contain any words.
     */
    boolean isEmpty();

    /**
     * Returns the root {@link TrieNode}. {@link TrieNode}s are <b>immutable</b>.
     */
    TrieNode<T> getRoot();

    /**
     * Returns 'true' if this {@link Trie} contains the word.<br>
     * If this returned 'true', {@link #get(String)} must return a value for which
     * {@link Optional#isPresent()} is 'true'.
     */
    boolean contains(String word);

    /**
     * Returns the value associated with the word.
     * If no value was associated with the word, the returned value's
     * {@link Optional#isPresent()} will return 'false'.
     */
    Optional<T> get(String word);

    /**
     * Returns all the words in this {@link Trie}.
     */
    Collection<String> getWords();

    /**
     * Returns all the values in this {@link Trie}.
     */
    Collection<T> getValues();

    /**
     * Calls {@link TrieVisitor#visit(String, Object)}
     * for each word-value pair in this {@link Trie}.
     */
    void visitWords(TrieVisitor<T> visitor);

    /**
     * Returns the longest common prefix in this {@link Trie}.<br>
     * For example, for a {@link Trie} with the words 'abc', 'abd' and 'abcd', the longest common prefix is 'ab'.
     */
    String getLongestPrefix();

    /**
     * Returns a {@link Trie} with words that start with the prefix. <br>
     * If no such words exist in the {@link Trie}, an empty {@link Trie} is returned.<br>
     * Does not alter this {@link Trie}.
     */
    Trie<T> subTrie(String prefix);

    /**
     * Returns a {@link Trie} in which the value of each word was transformed by calling {@link Function#apply(Object)}.<br>
     * If the result of the transformation returned 'null', that word and value will not appear in the returned {@link Trie}.<br>
     * Does not alter this {@link Trie}.
     */
    <A> Trie<A> map(Function<T, A> function);

    /**
     * Returns a {@link Trie} in which all values for which {@link Predicate#apply(Object)} returned 'false' are removed.<br>
     * Does not alter this {@link Trie}.
     */
    Trie<T> filter(Predicate<T> filter);

    /**
     * Returns a {@link Trie} which contains words and values from this {@link Trie} and the other {@link Trie}.<br>
     * If a word was contained in both {@link Trie}s, the returned {@link Trie} will map that word to either one
     * of the possible values, without any guarantees as to which one.<br>
     * Does not alter this {@link Trie}
     */
    Trie<T> union(Trie<T> other);

    /**
     * Returns a {@link Map} that contains this {@link Trie}'s word-value mappings.
     */
    Map<String, T> toMap();
}
