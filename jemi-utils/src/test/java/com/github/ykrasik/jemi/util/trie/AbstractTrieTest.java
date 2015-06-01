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

import com.github.ykrasik.jemi.util.function.Function;
import com.github.ykrasik.jemi.util.function.Predicate;
import com.github.ykrasik.jemi.util.opt.Opt;
import org.junit.Before;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Yevgeny Krasik
 */
public class AbstractTrieTest {
    protected Map<String, String> valueMap;
    protected TrieBuilder<String> builder;
    protected Trie<String> trie;

    @Before
    public void setUp() {
        this.valueMap = new HashMap<>();
        this.builder = new TrieBuilder<>();
        this.trie = null;
    }

    protected void buildTrie(String... words) {
        buildTrie(Arrays.asList(words));
    }

    protected void buildTrie(List<String> words) {
        for (String word : words) {
            addWord(word, word);
        }
        build();
    }

    protected void addWord(String word, String value) {
        valueMap.put(word, value);
        builder.set(word, value);
    }

    protected void build() {
        this.trie = builder.build();
    }

    protected void successfulSubTrie(String prefix) {
        trie = trie.subTrie(prefix);
        assertFalse("No subTrie for prefix: " + prefix, trie.isEmpty());
    }

    protected void failedSubTrie(String prefix) {
        assertTrue("Unexpected subTrie for prefix: " + prefix, trie.subTrie(prefix).isEmpty());
    }

    protected void map(Function<String, String> function) {
        trie = trie.mapValues(function);
    }

    protected void filter(Predicate<String> filter) {
        trie = trie.filter(filter);
    }

    protected void assertEmpty() {
        assertTrue("Trie isn't empty!", trie.isEmpty());
    }

    protected void assertNotEmpty() {
        assertFalse("Trie is empty!", trie.isEmpty());
    }

    protected void assertTrieSize(int expectedSize) {
        assertEquals("Invalid trie size!", expectedSize, trie.size());
    }

    protected void assertWords(String... expectedWords) {
        assertWords(Arrays.asList(expectedWords));
    }

    protected void assertWords(List<String> expectedWords) {
        if (expectedWords.isEmpty()) {
            assertEmpty();
        } else {
            assertNotEmpty();
        }
        assertTrieSize(expectedWords.size());

        doAssertWords(expectedWords);
    }

    protected void doAssertWords(List<String> expectedWords) {
        for (String word : expectedWords) {
            final String expectedValue = valueMap.get(word);
            assertNotNull("No expected value set for word: " + word, expectedValue);

            final Opt<String> value = trie.get(word);
            assertTrue("No value set for word: " + value, value.isPresent());

            assertEquals("Value mismatch!", expectedValue, value.get());
        }
        assertEquals(expectedWords.size(), trie.size());
    }

    protected void assertInvalidWords(String... invalidWords) {
        assertInvalidWords(Arrays.asList(invalidWords));
    }

    protected void assertInvalidWords(List<String> invalidWords) {
        for (String word : invalidWords) {
            assertFalse("Trie contains an invalid value!", trie.get(word).isPresent());
        }
    }

    protected void assertInvalidEmptyWords() {
        assertInvalidWords("", " ", "  ", "   ", "\t");
    }

    protected void assertLongestPrefix(String expectedPrefix) {
        assertEquals("Invalid longest prefix!", expectedPrefix, trie.getLongestPrefix());
    }
}
