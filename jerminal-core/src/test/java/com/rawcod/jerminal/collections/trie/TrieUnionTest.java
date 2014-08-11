package com.rawcod.jerminal.collections.trie;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * User: ykrasik
 * Date: 11/08/2014
 * Time: 21:54
 */
public class TrieUnionTest extends AbstractTrieTest {
    @Test
    public void testSimpleUnion() {
        final Trie<String> trie1 = createTrie("word");
        final Trie<String> trie2 = createTrie("anotherWord");

        this.trie = trie1.union(trie2);
        assertNotEmpty();
        assertWords("word", "anotherWord");
        assertLongestPrefix("");
    }

    @Test
    public void prefixUnionTest() {
        final Trie<String> trie1 = createTrie("word1");
        final Trie<String> trie2 = createTrie("word2");

        this.trie = trie1.union(trie2);
        assertNotEmpty();
        assertWords("word1", "word2");
        assertLongestPrefix("word");
    }

    @Test
    public void emptyUnionTest() {
        final Trie<String> trie1 = createTrie("single");
        final Trie<String> trie2 = createTrie();

        this.trie = trie1.union(trie2);
        assertNotEmpty();
        assertWords("single");
        assertLongestPrefix("single");
        assertSame(trie1, this.trie);

        this.trie = trie2.union(trie1);
        assertNotEmpty();
        assertWords("single");
        assertLongestPrefix("single");
        assertSame(trie2, this.trie);
    }

    @Test
    public void compoundUnionTest() {
        final Trie<String> trie1 = createTrie("word1", "another1", "extra");
        final Trie<String> trie2 = createTrie("word2");
        final Trie<String> trie3 = createTrie("another2", "extra2");
        final Trie<String> trie4 = createTrie("newWord", "many", "other", "words");

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

    private Trie<String> createTrie(String... words) {
        final TrieBuilder<String> builder = new TrieBuilder<>();
        for (String word : words) {
            builder.add(word, word);
        }
        return builder.build();
    }

    @Override
    protected void assertWords(String... expectedWords) {
        assertEquals("Words mismatch!", Sets.newHashSet(expectedWords), new HashSet<>(trie.getWords()));
    }
}
