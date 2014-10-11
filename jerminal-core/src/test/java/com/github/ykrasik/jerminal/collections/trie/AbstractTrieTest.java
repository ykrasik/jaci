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
import com.google.common.collect.Maps;
import org.junit.Before;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Yevgeny Krasik
 */
public class AbstractTrieTest {
    private Map<String, String> valueMap;
    protected Trie<String> trie;

    @Before
    public void setUp() {
        this.valueMap = new HashMap<>();
        this.trie = new TrieImpl<>();
    }

    protected void addWord(String word, String value) {
        valueMap.put(word, value);
        trie = trie.set(word, value);
    }

    protected void successfulSubTrie(String prefix) {
        trie = trie.subTrie(prefix);
        assertFalse("No subTrie for prefix: " + prefix, trie.isEmpty());
    }

    protected void failedSubTrie(String prefix) {
        assertTrue("Unexpected subTrie for prefix: " + prefix, trie.subTrie(prefix).isEmpty());
    }

    protected void assertEmpty() {
        assertTrue("Trie isn't empty!", trie.isEmpty());
    }

    protected void assertNotEmpty() {
        assertFalse("Trie is empty!", trie.isEmpty());
    }

    protected void assertWords(String... expectedWords) {
        final Map<String, String> expectedValueMap = Maps.toMap(Arrays.asList(expectedWords), new Function<String, String>() {
            @Override
            public String apply(String input) {
                return valueMap.get(input);
            }
        });

        assertEquals(expectedValueMap, trie.toMap());
//        final Set<String> expectedWordsSet = Sets.newHashSet(expectedWords);
//        assertEquals("Words mismatch!", expectedWordsSet, new HashSet<>(trie.getWords()));
//
//        final Map<String, String> expectedValueMap = Maps.filterKeys(this.valueMap, new Predicate<String>() {
//            @Override
//            public boolean apply(String input) {
//                return expectedWordsSet.contains(input);
//            }
//        });
//        final Set<String> expectedValues = new HashSet<>(expectedValueMap.values());
//        assertEquals("Values mismatch!", expectedValues, new HashSet<>(trie.values()));
    }

    protected void assertLongestPrefix(String expectedPrefix) {
        assertEquals("Invalid longest prefix!", expectedPrefix, trie.getLongestPrefix());
    }
}
