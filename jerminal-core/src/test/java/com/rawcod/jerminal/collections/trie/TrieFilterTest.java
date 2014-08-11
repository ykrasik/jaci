package com.rawcod.jerminal.collections.trie;

import com.google.common.base.Predicate;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * User: ykrasik
 * Date: 11/08/2014
 * Time: 21:47
 */
public class TrieFilterTest extends AbstractTrieTest {
    // This filter considers empty values illegal.
    private final Predicate<String> emptyFilter = new Predicate<String>() {
        @Override
        public boolean apply(String input) {
            return !input.isEmpty();
        }
    };

    @Override
    @Before
    public void setUp() {
        super.setUp();

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

        build();
    }

    @Test
    public void testFilter1() {
        // Root
        assertNotEmpty();
        assertWords("valid1", "valid2", "invalid1", "ok", "invalid2", "legal", "notOkValue", "thisValueIsOk",
            "thisValueIsNotOk", "thisValueIsOj", "thisValueIsOk2", "thisValueIsNotOj", "thisValueIsNotOk2");
        assertLongestPrefix("");
    }

    @Test
    public void testFilter2() {
        // Filter root - Mixed legal and illegal values.
        successfulFilter("");
        assertNotEmpty();
        assertWords("valid1", "valid2", "ok", "legal", "thisValueIsOk", "thisValueIsOj", "thisValueIsOk2");
        assertLongestPrefix("");
    }

    @Test
    public void testFilter3() {
        // "val" - Only legal values with this prefix.
        successfulFilter("val");
        assertNotEmpty();
        assertWords("valid1", "valid2");
        assertLongestPrefix("valid");
    }

    @Test
    public void testFilter4() {
        // "legal" - Only 1 legal value with this prefix.
        successfulFilter("legal");
        assertNotEmpty();
        assertWords("legal");
        assertLongestPrefix("legal");
    }

    @Test
    public void testFilter5() {
        // "this" - Mixed legal and illegal values.
        successfulFilter("this");
        assertNotEmpty();
        assertWords("thisValueIsOk", "thisValueIsOj", "thisValueIsOk2");
        assertLongestPrefix("thisValueIsO");
    }

    @Test
    public void testFailedFilter() {
        // All values are invalid, no subTrie expected after filtering.
        failedFilter("n"); // Only 1 value and it's illegal.
        failedFilter("notOkValue"); // Only 1 value and it's illegal.
        failedFilter("in"); // invalid1, invalid2
        failedFilter("thisValueIsNot"); // thisValueIsNotOk, thisValueIsNotOj, thisValueIsNotOk2
    }

    private void successfulFilter(String prefix) {
        trie = filter(prefix);
        assertFalse("No subTrie for filtered prefix: " + prefix, trie.isEmpty());
    }

    private void failedFilter(String prefix) {
        assertFalse("Unexpected subTrie for filtered prefix: " + prefix, filter(prefix).isEmpty());
    }

    private Trie<String> filter(String prefix) {
        successfulSubTrie(prefix);
        return trie.filter(emptyFilter);
    }
}
