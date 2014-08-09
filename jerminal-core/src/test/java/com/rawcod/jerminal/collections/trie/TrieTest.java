package com.rawcod.jerminal.collections.trie;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * User: ykrasik
 * Date: 08/08/2014
 * Time: 12:06
 */
public class TrieTest {
    private Trie<String> trie;
    private ReadOnlyTrie<String> currentSubTrie;
    private Map<String, String> expectedWordMap;

    // This filter considers empty values illegal.
    private final Predicate<String> emptyFilter = new Predicate<String>() {
        @Override
        public boolean apply(String input) {
            return !input.isEmpty();
        }
    };

    @Before
    public void setUp() throws Exception {
        this.trie = new TrieImpl<>();
        this.currentSubTrie = trie;
        this.expectedWordMap = new HashMap<>();
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
        successfulSubTrie("p");
        assertNotEmpty();
        assertWords("p", "pr", "pre", "pre1fix", "pre2", "prefix", "prefiz");
        assertLongestPrefix("p");

        // "pr"
        successfulSubTrie("pr");
        assertNotEmpty();
        assertWords("pr", "pre", "pre1fix", "pre2", "prefix", "prefiz");
        assertLongestPrefix("pr");

        // "pre"
        successfulSubTrie("pre");
        assertNotEmpty();
        assertWords("pre", "pre1fix", "pre2", "prefix", "prefiz");
        assertLongestPrefix("pre");

        // "pre1" - only "pre1fix" is possible from here.
        successfulSubTrie("pre1");
        assertNotEmpty();
        assertWords("pre1fix");
        assertLongestPrefix("pre1fix");

        // "pre2" - only "pre2" is possible from here.
        successfulSubTrie("pre2");
        assertNotEmpty();
        assertWords("pre2");
        assertLongestPrefix("pre2");

        // "pre2" - only "pre2" is possible from here.
        successfulSubTrie("pre2");
        assertNotEmpty();
        assertWords("pre2");
        assertLongestPrefix("pre2");

        // "pref"
        successfulSubTrie("pref");
        assertNotEmpty();
        assertWords("prefix", "prefiz");
        assertLongestPrefix("prefi");

        // "prefi"
        successfulSubTrie("prefi");
        assertNotEmpty();
        assertWords("prefix", "prefiz");
        assertLongestPrefix("prefi");

        // "prefix"
        successfulSubTrie("prefix");
        assertNotEmpty();
        assertWords("prefix");
        assertLongestPrefix("prefix");

        // "prefiz"
        successfulSubTrie("prefiz");
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
    public void filterTest() {
        addWord("valid1", "value1");
        addWord("valid2", "value2");
        addWord("invalid1", "");
        addWord("ok", "value3");
        addWord("invalid2", "");
        addWord("legal", "value4");
        addWord("notOkValue", "");
        addWord("thisValueIsOk", "value5");
        addWord("thisValueIsNotOk", "");
        addWord("thisValueIsOj", "value6");
        addWord("thisValueIsOk2", "value7");
        addWord("thisValueIsNotOj", "");
        addWord("thisValueIsNotOk2", "");

        // Root
        assertNotEmpty();
        assertWords("valid1", "valid2", "invalid1", "ok", "invalid2", "legal", "notOkValue", "thisValueIsOk",
                    "thisValueIsNotOk", "thisValueIsOj", "thisValueIsOk2", "thisValueIsNotOj", "thisValueIsNotOk2");
        assertLongestPrefix("");

        // Filter root - Mixed legal and illegal values.
        successfulFilter("");
        assertNotEmpty();
        assertWords("valid1", "valid2", "ok", "legal", "thisValueIsOk", "thisValueIsOj", "thisValueIsOk2");
        assertLongestPrefix("");

        // "val" - Only legal values with this prefix.
        successfulFilter("val");
        assertNotEmpty();
        assertWords("valid1", "valid2");
        assertLongestPrefix("valid");

        // "legal" - Only 1 legal value with this prefix.
        successfulFilter("legal");
        assertNotEmpty();
        assertWords("legal");
        assertLongestPrefix("legal");

        // "this" - Mixed legal and illegal values.
        successfulFilter("this");
        assertNotEmpty();
        assertWords("thisValueIsOk", "thisValueIsOj", "thisValueIsOk2");
        assertLongestPrefix("thisValueIsO");

        // all values are invalid, no subTrie expected after filtering.
        failedFilter("n"); // Only 1 value and it's illegal.
        failedFilter("notOkValue"); // Only 1 value and it's illegal.
        failedFilter("in"); // invalid1, invalid2
        failedFilter("thisValueIsNot"); // thisValueIsNotOk, thisValueIsNotOj, thisValueIsNotOk2
    }

    private void addWord(String word, String value) {
        trie.put(word, value);
        expectedWordMap.put(word, value);
    }

    private void successfulSubTrie(String prefix) {
        final Optional<ReadOnlyTrie<String>> subTrie = subTrie(prefix);
        assertTrue("No subTrie for prefix: " + prefix, subTrie.isPresent());
        this.currentSubTrie = subTrie.get();
    }

    private void failedSubTrie(String prefix) {
        assertFalse("Unexpected subTrie for prefix: " + prefix, subTrie(prefix).isPresent());
    }

    private Optional<ReadOnlyTrie<String>> subTrie(String prefix) {
        return trie.subTrie(prefix);
    }

    private void successfulFilter(String prefix) {
        assertTrue("No subTrie for filtered prefix: " + prefix, filter(prefix));
    }

    private void failedFilter(String prefix) {
        assertFalse("Unexpected subTrie for filtered prefix: " + prefix, filter(prefix));
    }

    private boolean filter(String prefix) {
        successfulSubTrie(prefix);
        final Optional<ReadOnlyTrie<String>> filteredTrie = currentSubTrie.filter(emptyFilter);
        if (!filteredTrie.isPresent()) {
            return false;
        }
        this.currentSubTrie = filteredTrie.get();
        return true;
    }

    private void assertWords(String... expectedWords) {
        final Set<String> expectedWordsSet = Sets.newHashSet(expectedWords);
        assertEquals("Words mismatch!", expectedWordsSet, new HashSet<>(currentSubTrie.getAllWords()));

        final Map<String, String> expectedValueMap = Maps.filterKeys(this.expectedWordMap, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return expectedWordsSet.contains(input);
            }
        });
        final Set<String> expectedValues = new HashSet<>(expectedValueMap.values());
        assertEquals("Values mismatch!", expectedValues, new HashSet<>(currentSubTrie.getAllValues()));
    }

    private void assertEmpty() {
        assertTrue("Trie isn't empty!", currentSubTrie.isEmpty());
    }

    private void assertNotEmpty() {
        assertFalse("Trie is empty!", currentSubTrie.isEmpty());
    }

    private void assertLongestPrefix(String expectedPrefix) {
        assertEquals("Invalid longest prefix!", expectedPrefix, currentSubTrie.getLongestPrefix());
    }
}
