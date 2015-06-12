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

import com.github.ykrasik.jaci.util.function.Func;
import com.github.ykrasik.jaci.util.function.Pred;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Yevgeny Krasik
 */
public class TrieBasicTest extends AbstractTrieTest {
    @Test
    public void emptyTrieTest() {
        buildAndAssertTrie();

        assertInvalidEmptyWords();
        assertInvalidWords("", " ");
        assertInvalidWords(generateWords(1));
    }

    @Test
    public void singleLetterTrieTest() {
        buildAndAssertTrie(generateWords(1));

        assertInvalidEmptyWords();
        assertInvalidWords("e", "f");
        assertInvalidWords(generateWords(2));
    }

    @Test
    public void doubleLetterTrieTest() {
        final List<String> words = generateWords(2);
        final List<String> removedWords = removeRandomWords(words, 3);
        buildAndAssertTrie(words);

        assertInvalidWords(removedWords);
        assertInvalidEmptyWords();
        assertInvalidWords("ae", "be", "ce", "de", "ee");
        assertInvalidWords(generateWords(1));
        assertInvalidWords(generateWords(3));
    }

    @Test
    public void tripleLetterTrieTest() {
        final List<String> words = generateWords(3);
        final List<String> removedWords = removeRandomWords(words, 15);
        buildAndAssertTrie(words);

        assertInvalidWords(removedWords);
        assertInvalidEmptyWords();
        assertInvalidWords("aae", "bbe", "cce", "dde", "eee");
        assertInvalidWords(generateWords(1));
        assertInvalidWords(generateWords(2));
        assertInvalidWords(generateWords(4));
    }

    @Test
    public void quadrupleLetterTrieTest() {
        final List<String> words = generateWords(4);
        final List<String> removedWords = removeRandomWords(words, 50);
        buildAndAssertTrie(words);

        assertInvalidWords(removedWords);
        assertInvalidEmptyWords();
        assertInvalidWords("aaae", "bbbe", "ccce", "ddde", "eeee");
        assertInvalidWords(generateWords(1));
        assertInvalidWords(generateWords(2));
        assertInvalidWords(generateWords(3));
        assertInvalidWords(generateWords(5));
    }

    @Test
    public void testMap() {
        buildAndAssertTrie("1", "2", "3");

        // A function that adds 1 to it's input.
        map(new Func<String, String>() {
            @Override
            public String apply(String input) {
                return String.valueOf(Integer.parseInt(input) + 1);
            }
        });

        assertNotEmpty();
        assertTrieSize(3);
        assertEquals("2", trie.get("1").get());
        assertEquals("3", trie.get("2").get());
        assertEquals("4", trie.get("3").get());
    }

    @Test
    public void testFilter() {
        buildAndAssertTrie("invalid1", "a", "invalid2", "b", "cd", "invalidity");

        // Filter all values starting with "invalid"
        filter(new Pred<String>() {
            @Override
            public boolean test(String input) {
                return !input.startsWith("invalid");
            }
        });

        assertWords("a", "b", "cd");
    }

    @Test
    public void testVisitWords() {
        buildAndAssertTrie("a", "b", "c");

        final Map<String, String> visited = new HashMap<>();
        trie.visitWords(new TrieVisitor<String>() {
            @Override
            public void visit(String word, String value) {
                visited.put(word, value);
            }
        });

        assertEquals("a", visited.get("a"));
        assertEquals("b", visited.get("b"));
        assertEquals("c", visited.get("c"));
    }

    @Test
    public void testToMap() {
        buildAndAssertTrie("a", "b", "c");

        final Map<String, String> map = trie.toMap();

        assertEquals("a", map.get("a"));
        assertEquals("b", map.get("b"));
        assertEquals("c", map.get("c"));
    }

    private List<String> generateWords(int length) {
        return generateWords(4, length);
    }

    private List<String> generateWords(int numChars, int length) {
        return new LinkedList<>(new StringGenerator(numChars).generateAllFixedLengthStringPermutations(length));
    }

    private List<String> removeRandomWords(List<String> words, int numWords) {
        final Random random = new Random(System.currentTimeMillis());
        final List<String> removedWords = new ArrayList<>(numWords);
        final int max = words.size();
        for (int i = 0; i < numWords; i++) {
            final int index = random.nextInt(max - i);
            removedWords.add(words.remove(index));
        }
        return removedWords;
    }

    private void buildAndAssertTrie(String... words) {
        buildAndAssertTrie(Arrays.asList(words));
    }

    private void buildAndAssertTrie(List<String> words) {
        buildTrie(words);
        assertWords(words);
    }
}
