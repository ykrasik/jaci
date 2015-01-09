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

import org.junit.Test;

import java.util.List;

/**
 * @author Yevgeny Krasik
 */
public class TrieBasicTest extends AbstractTrieTest {
    @Test
    public void emptyTrieTest() {
        buildAndAssertTrie();

        assertLongestPrefix("");
    }

    @Test
    public void singleLetterTrieTest() {
        buildAndAssertTrie("a", "b", "c", "d");

        assertInvalidWords(
            "", " ",
            "e", "f",
            "ab", "ba", "cd", "de"
        );
    }

    @Test
    public void doubleLetterTrieTest() {
        buildAndAssertTrie("aa", "ab", "bb", "ba", "cd", "de");

        assertInvalidWords(
            "", " ",
            "a", "b", "c", "d", "e", "f",
            "ac", "ad", "bc", "bd", "ca", "cb", "cc", "da", "db", "dc", "dd",
            "aaa", "abc", "abb", "aba", "acd",
            "aabb"
        );
    }

    @Test
    public void tripleLetterTrieTest() {
        buildAndAssertTrie("aaa", "aab", "aac", "aba", "abb", "abc", "aca", "bbb", "baa", "cac", "ccc");

        assertInvalidWords(
            "", " ",
            "a", "b", "c", "d", "e", "f",
            "aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cd", "dd",
            "bab", "bac", "bbc", "bcc", "caa", "cab", "cba", "cbb", "cbc", "cca", "ccb", "ddd",
            "aaaa", "bbbb", "aabb", "aaca",
            "aaabbb"
        );
    }

    @Test
    public void quadrupleLetterTrieTest() {
        buildAndAssertTrie(
            "aaaa", "aaab", "aaac",
            "aaba", "aabb", "aabc",
            "aaca", "aacb", "aacc",
            "abaa", "abab", "abac",
            "abba", "abbb", "abbc",
            "abca", "abcb", "abcc",
            "acaa", "acab", "acac",
            "acba", "acbb", "acbc",
            "acca", "accb", "accc",
            "bbbb", "bbbc", "bbcc", "bccc", "cccc", "abcd"
        );

        assertInvalidWords(
            "", " ",
            "a", "b", "c", "d", "e", "f",
            "aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc", "dd",
            "aaa", "aab", "aac", "aba", "abb", "abc", "aca", "acb", "acc",
            "baa", "bab", "bac", "bba", "bbb", "bbc", "bca", "bcb", "bcc",
            "ccc", "ddd", "bcd",
            "aaad", "bbbd", "cccd", "bbba", "bbaa", "baaa"
        );
    }

    @Test
    public void test() {
        long start = System.currentTimeMillis();
        final List<String> strings = new StringGenerator(3).generateAllFixedLengthStringPermutations(4);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Generated " + strings.size() + " entries. TimeTaken = " + elapsed + "ms");

        final String[] array = new String[strings.size()];
        strings.toArray(array);

        buildAndAssertTrie(array);
    }

    // TODO: Add more tests.
}
