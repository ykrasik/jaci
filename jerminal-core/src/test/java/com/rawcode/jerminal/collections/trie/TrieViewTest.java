package com.rawcode.jerminal.collections.trie;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.rawcod.jerminal.collections.trie.Trie;
import com.rawcod.jerminal.collections.trie.TrieImpl;
import com.rawcod.jerminal.collections.trie.TrieView;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: ykrasik
 * Date: 08/08/2014
 * Time: 14:10
 */
public class TrieViewTest {
    private Trie<String> trie;
    private TrieView currentTrieView;

    @Before
    public void setUp() throws Exception {
        this.trie = new TrieImpl<>();
        this.currentTrieView = trie.trieView();
    }

    @Test
    public void emptyTrieTest() {
        assertEmpty();
        assertWords();
        assertLongestPrefix("");
    }

    @Test
    public void prefixTest() {
        addWord("p", "prefix1");
        addWord("pr", "prefix2");
        addWord("pre", "prefix3");
        addWord("pre1fix", "prefix4");
        addWord("pre2", "prefix5");
        addWord("prefix", "prefix6");
        addWord("prefiz", "prefix7");
        addWord("other", "other");

        // Root
        assertNotEmpty();
        assertWords("p", "pr", "pre", "pre1fix", "pre2", "prefix", "prefiz", "other");
        assertLongestPrefix("");

        // "p"
        successfulTrieView("p");
        assertNotEmpty();
        assertWords("p", "pr", "pre", "pre1fix", "pre2", "prefix", "prefiz");
        assertLongestPrefix("p");

        // "pr"
        successfulTrieView("pr");
        assertNotEmpty();
        assertWords("pr", "pre", "pre1fix", "pre2", "prefix", "prefiz");
        assertLongestPrefix("pr");

        // "pre"
        successfulTrieView("pre");
        assertNotEmpty();
        assertWords("pre", "pre1fix", "pre2", "prefix", "prefiz");
        assertLongestPrefix("pre");

        // "pre1" - only "pre1fix" is possible from here.
        successfulTrieView("pre1");
        assertNotEmpty();
        assertWords("pre1fix");
        assertLongestPrefix("pre1fix");

        // "pre2" - only "pre2" is possible from here.
        successfulTrieView("pre2");
        assertNotEmpty();
        assertWords("pre2");
        assertLongestPrefix("pre2");

        // "pre2" - only "pre2" is possible from here.
        successfulTrieView("pre2");
        assertNotEmpty();
        assertWords("pre2");
        assertLongestPrefix("pre2");

        // "pref"
        successfulTrieView("pref");
        assertNotEmpty();
        assertWords("prefix", "prefiz");
        assertLongestPrefix("prefi");

        // "prefi"
        successfulTrieView("prefi");
        assertNotEmpty();
        assertWords("prefix", "prefiz");
        assertLongestPrefix("prefi");

        // "prefix"
        successfulTrieView("prefix");
        assertNotEmpty();
        assertWords("prefix");
        assertLongestPrefix("prefix");

        // "prefiz"
        successfulTrieView("prefiz");
        assertNotEmpty();
        assertWords("prefiz");
        assertLongestPrefix("prefiz");

        // Invalid prefixes
        failedSubTrie("prefix1");
        failedSubTrie("pred");
        failedSubTrie("predix");
        failedSubTrie("ob");
        failedSubTrie("othar");
        failedSubTrie("others");
    }

    @Test
    public void simpleUnionTest() {
        final TrieView view1 = createTrieView("word");
        final TrieView view2 = createTrieView("anotherWord");

        final TrieView union = view1.union(view2);
        assertNotEmpty(union);
        assertWords(union, "word", "anotherWord");
        assertLongestPrefix(union, "");
    }

    @Test
    public void prefixUnionTest() {
        final TrieView view1 = createTrieView("word1");
        final TrieView view2 = createTrieView("word2");

        final TrieView union = view1.union(view2);
        assertNotEmpty(union);
        assertWords(union, "word1", "word2");
        assertLongestPrefix(union, "word");
    }

    @Test
    public void emptyUnionTest() {
        final TrieView view1 = createTrieView("single");
        assertNotEmpty(view1);
        final TrieView view2 = createTrieView();
        assertEmpty(view2);

        final TrieView union = view1.union(view2);
        assertNotEmpty(union);
        assertWords(union, "single");
        assertLongestPrefix(union, "single");
    }

    @Test
    public void compoundUnionTest() {
        final TrieView view1 = createTrieView("word1", "another1", "extra");
        final TrieView view2 = createTrieView("word2");
        final TrieView view3 = createTrieView("another2", "extra2");
        final TrieView view4 = createTrieView("newWord", "many", "other", "words");

        final TrieView union1 = view1.union(view2);
        assertNotEmpty(union1);
        assertWords(union1, "word1", "word2", "another1", "extra");
        assertLongestPrefix(union1, "");

        successfulTrieView(union1, "w");
        assertWords("word1", "word2");
        assertLongestPrefix("word");

        final TrieView union2 = view3.union(view4);
        assertNotEmpty(union2);
        assertWords(union2, "another2", "extra2", "newWord", "many", "other", "words");
        assertLongestPrefix(union2, "");

        successfulTrieView(union2, "a");
        assertWords("another2");
        assertLongestPrefix("another2");

        final TrieView union3 = union1.union(union2);
        assertNotEmpty(union3);
        assertWords(union3, "word1", "word2", "another1", "extra", "another2", "extra2", "newWord", "many", "other", "words");
        assertLongestPrefix(union3, "");

        successfulTrieView(union3, "n");
        assertWords("newWord");
        assertLongestPrefix("newWord");
    }

    private TrieView createTrieView(String... words) {
        final Trie<String> trie = new TrieImpl<>();
        for (String word : words) {
            trie.put(word, "");
        }
        return trie.trieView();
    }

    private void addWord(String word, String value) {
        trie.put(word, value);
    }

    private void successfulTrieView(String prefix) {
        successfulTrieView(trie.trieView(), prefix);
    }

    private void successfulTrieView(TrieView trieView, String prefix) {
        final Optional<TrieView> prefixTrieView = trieView.subTrie(prefix);
        assertTrue("No trieView for prefix: " + prefix, prefixTrieView.isPresent());
        this.currentTrieView = prefixTrieView.get();
    }

    private void failedSubTrie(String prefix) {
        failedSubTrie(trie.trieView(), prefix);
    }

    private void failedSubTrie(TrieView trieView, String prefix) {
        assertFalse("Unexpected trieView for prefix: " + prefix, trieView.subTrie(prefix).isPresent());
    }

    private void assertWords(String... expectedWords) {
        assertWords(currentTrieView, expectedWords);
    }

    private void assertWords(TrieView trieView, String... expectedWords) {
        final Set<String> expectedWordsSet = Sets.newHashSet(expectedWords);
        assertEquals("Words mismatch!", expectedWordsSet, new HashSet<>(trieView.getAllWords()));
    }

    private void assertEmpty() {
        assertEmpty(currentTrieView);
    }

    private void assertEmpty(TrieView trieView) {
        assertTrue("TrieView isn't empty!", trieView.isEmpty());
    }

    private void assertNotEmpty() {
        assertNotEmpty(currentTrieView);
    }

    private void assertNotEmpty(TrieView trieView) {
        assertFalse("TrieView is empty!", trieView.isEmpty());
    }

    private void assertLongestPrefix(String expectedPrefix) {
        assertLongestPrefix(currentTrieView, expectedPrefix);
    }

    private void assertLongestPrefix(TrieView trieView, String expectedPrefix) {
        assertEquals("Invalid longest prefix!", expectedPrefix, trieView.getLongestPrefix());
    }
}
