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

/**
 * An <b>immutable</b> node in a {@link com.github.ykrasik.jerminal.collections.trie.Trie Trie}.<br>
 * Holds the node's character, value and children.
 *
 * @author Yevgeny Krasik
 */
public interface TrieNode<T> {
    /**
     * @return True if this node represents a word.<br>
     *         If true, {@link #getValue()} must return a non-null value.
     */
    boolean isWord();

    /**
     * @return The number of words that are reachable from this node.
     */
    // FIXME: Test this one!
    int getNumWords();

    /**
     * @return True if this node is not a word and does not have any children.
     */
    boolean isEmpty();

    /**
     * @return The character associated with this node.
     */
    char getCharacter();

    /**
     * @return The value contained in this node.
     */
    Optional<T> getValue();

    /**
     * @return A copy of this node with the value set. The value can be null.<br>
     *         Does not alter this node.
     */
    TrieNode<T> setValue(T value);

    /**
     * @return A child node associated with the character.<br>
     *         <b>Case insensitive</b>
     */
    Optional<TrieNode<T>> getChild(char c);

    /**
     * @return A copy of this node that has the given child as a child node. Will overwrite any existing child node
     *         with the same character.<br>
     *         Does not alter this node.
     */
    TrieNode<T> setChild(TrieNode<T> child);

    /**
     * @return A collection of all this node's children nodes.
     */
    Collection<TrieNode<T>> getChildren();

    /**
     * @return A copy of this node in which the value (if present) and all the node's children are transformed by
     *         calling {@link Function#apply(Object)}.<br>
     *         If the result of the transformation returns 'null' for the node's value and for all it's children,
     *         that node will not be present in the returned node.
     *         Does not alter this node.
     */
    <A> Optional<TrieNode<A>> map(Function<T, A> function);

    /**
     * @return A copy of this node that is a union with the other node. The {@link #getValue()} of the returned node is either
     *         this node's value or the other node's value, without guarantees. The node's children also
     *         undergo this union process.<br>
     *         Does not alter this node.
     * @throws java.lang.IllegalArgumentException If the {@link #getCharacter()} of this node and the other node are different.
     */
    TrieNode<T> union(TrieNode<T> other);
}
