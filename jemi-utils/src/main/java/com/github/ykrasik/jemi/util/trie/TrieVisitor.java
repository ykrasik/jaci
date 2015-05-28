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

/**
 * Can be given to a {@link Trie} to visit all it's words and their associated values.
 *
 * @author Yevgeny Krasik
 */
public interface TrieVisitor<T> {
    /**
     * Called for each word-value mapping in a Trie.
     *
     * @param word The word in a word-value mapping.
     * @param value The value in a word-value mapping.
     */
    void visit(String word, T value);
}
