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

package com.github.ykrasik.jerminal.util.trie;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Yevgeny Krasik
 */
public class TrieUnionTest extends AbstractTrieTest {
    @Test
    public void testSimpleUnion() {
        final Trie<String> trie1 = Tries.toStringTrie("one");
        final Trie<String> trie2 = Tries.toStringTrie("two");

        assertUnion(trie1, trie2, "", "one", "two");
    }

    @Test
    public void prefixUnionTest() {
        final Trie<String> trie1 = Tries.toStringTrie("word1");
        final Trie<String> trie2 = Tries.toStringTrie("word2");

        assertUnion(trie1, trie2, "word", "word1", "word2");
    }

    @Test
    public void emptyUnionTest() {
        final Trie<String> trie1 = Tries.toStringTrie("single");
        final Trie<String> trie2 = Tries.toStringTrie();

        assertUnion(trie1, trie2, "single", "single");
    }

    @Test
    public void testSameValue() {
        final Trie<String> trie1 = Tries.toStringTrie("single");
        final Trie<String> trie2 = Tries.toStringTrie("single");

        assertUnion(trie1, trie2, "single", "single");
    }

    @Test
    public void testSameValues() {
        final String[] words = { "one", "two", "three" };
        final Trie<String> trie1 = Tries.toStringTrie(words);
        final Trie<String> trie2 = Tries.toStringTrie(words);

        assertUnion(trie1, trie2, "", words);
    }

    @Test
    public void testCommonValues() {
        final Trie<String> trie1 = Tries.toStringTrie("one", "common");
        final Trie<String> trie2 = Tries.toStringTrie("common", "two");

        assertUnion(trie1, trie2, "", "one", "two", "common");
    }

    @Test
    public void testDifferentCase() {
        final Trie<String> trie1 = Tries.toStringTrie("one", "commonCase");
        final Trie<String> trie2 = Tries.toStringTrie("commoncase", "two");

        assertUnion(trie1, trie2, "", "one", "two", "commoncase", "commonCase");
    }

    @Test
    public void compoundUnionTest() {
        final Trie<String> trie1 = Tries.toStringTrie("word1", "another1", "extra");
        final Trie<String> trie2 = Tries.toStringTrie("word2");
        final Trie<String> trie3 = Tries.toStringTrie("another2", "extra2");
        final Trie<String> trie4 = Tries.toStringTrie("newWord", "many", "other", "words");

        final Trie<String> union1 = trie1.union(trie2);
        final Trie<String> union2 = trie3.union(trie4);
        final Trie<String> union3 = union1.union(union2);

        this.trie = union1;
        assertWords("word1", "word2", "another1", "extra");
        assertLongestPrefix("");

        successfulSubTrie("w");
        assertWords("word1", "word2");
        assertLongestPrefix("word");

        this.trie = union2;
        assertWords("another2", "extra2", "newWord", "many", "other", "words");
        assertLongestPrefix("");

        successfulSubTrie("a");
        assertWords("another2");
        assertLongestPrefix("another2");

        this.trie = union3;
        assertWords("word1", "word2", "another1", "extra", "another2", "extra2", "newWord", "many", "other", "words");
        assertLongestPrefix("");

        successfulSubTrie("n");
        assertWords("newWord");
        assertLongestPrefix("newWord");
    }

    private void assertUnion(Trie<String> trie1, Trie<String> trie2, String longestPrefix, String... words) {
        doAssertUnion(trie1, trie2, longestPrefix, words);
        doAssertUnion(trie2, trie1, longestPrefix, words);
    }

    private void doAssertUnion(Trie<String> trie1, Trie<String> trie2, String longestPrefix, String... words) {
        this.trie = trie1.union(trie2);
        assertWords(words);
        assertLongestPrefix(longestPrefix);
    }

    @Override
    protected void doAssertWords(List<String> expectedWords) {
        assertEquals("Words mismatch!", Sets.newHashSet(expectedWords), new HashSet<>(trie.words()));
    }
}
