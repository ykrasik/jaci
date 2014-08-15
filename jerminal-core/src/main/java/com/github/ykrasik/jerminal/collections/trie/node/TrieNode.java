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

/**
 * An <b>immutable</b> node in a {@link com.github.ykrasik.jerminal.collections.trie.Trie}.<br>
 * Holds information about the node (it's character and value) and children.
 *
 * @author Yevgeny Krasik
 */
public interface TrieNode<T> {
    /**
     * Returns 'true' if this node represents a word.<br>
     * For any node that returns 'true' to this,
     * {@link #getValue()} is expected to return a  non-null value.
     */
    boolean isWord();

    /**
     * Returns 'true' if this node is not a word and does not have any children.
     */
    boolean isEmpty();

    /**
     * Returns the character associated with this node.,
     */
    char getCharacter();

    /**
     * Returns the value contained in this node, or null if there isn't one.
     */
    T getValue();

    /**
     * Returns a child node associated with the character 'c', or null if there isn't one.<br>
     * <b>Case insensitive</b>
     */
    TrieNode<T> getChild(char c);

    /**
     * Returns a collection of all this node's children nodes.
     */
    Collection<TrieNode<T>> getChildren();
}
