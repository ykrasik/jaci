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

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link TrieVisitor} that stores all visited words and values in a map.
 *
 * @author Yevgeny Krasik
 */
public class MapTrieVisitor<T> implements TrieVisitor<T> {
    private final Map<String, T> values = new HashMap<>();

    @Override
    public void visit(String word, T value) {
        values.put(word, value);
    }

    /**
     * @return The collected word-value map.
     */
    public Map<String, T> getMap() {
        return values;
    }
}
