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

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Yevgeny Krasik
 */
public class TrieUnionTest extends AbstractTrieTest {
    @Test
    public void testSimpleUnion() {
        final Trie<String> trie1 = Tries.toStringTrie("word");
        final Trie<String> trie2 = Tries.toStringTrie("anotherWord");

        this.trie = trie1.union(trie2);
        assertNotEmpty();
        assertWords("word", "anotherWord");
        assertLongestPrefix("");
    }

    @Test
    public void prefixUnionTest() {
        final Trie<String> trie1 = Tries.toStringTrie("word1");
        final Trie<String> trie2 = Tries.toStringTrie("word2");

        this.trie = trie1.union(trie2);
        assertNotEmpty();
        assertWords("word1", "word2");
        assertLongestPrefix("word");
    }

    @Test
    public void emptyUnionTest() {
        final Trie<String> trie1 = Tries.toStringTrie("single");
        final Trie<String> trie2 = Tries.toStringTrie();

        this.trie = trie1.union(trie2);
        assertNotEmpty();
        assertWords("single");
        assertLongestPrefix("single");
        assertSame(trie1, this.trie);

        this.trie = trie2.union(trie1);
        assertNotEmpty();
        assertWords("single");
        assertLongestPrefix("single");
        assertSame(trie1, this.trie);
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
        assertNotEmpty();
        assertWords("word1", "word2", "another1", "extra");
        assertLongestPrefix("");

        successfulSubTrie("w");
        assertWords("word1", "word2");
        assertLongestPrefix("word");

        this.trie = union2;
        assertNotEmpty();
        assertWords("another2", "extra2", "newWord", "many", "other", "words");
        assertLongestPrefix("");

        successfulSubTrie("a");
        assertWords("another2");
        assertLongestPrefix("another2");

        this.trie = union3;
        assertNotEmpty();
        assertWords("word1", "word2", "another1", "extra", "another2", "extra2", "newWord", "many", "other", "words");
        assertLongestPrefix("");

        successfulSubTrie("n");
        assertWords("newWord");
        assertLongestPrefix("newWord");
    }

    // FIXME: Tests aren't exhaustive, what about a union of 2 tries with the same values?

    @Override
    protected void assertWords(String... expectedWords) {
        assertEquals("Words mismatch!", Sets.newHashSet(expectedWords), new HashSet<>(trie.words()));
    }
}
