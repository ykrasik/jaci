package com.rawcod.jerminal.collections.trie;

import org.junit.Test;

/**
 * User: ykrasik
 * Date: 11/08/2014
 * Time: 21:44
 */
public class TrieBasicTest extends AbstractTrieTest {
    @Test
    public void emptyTrieTest() {
        build();
        assertEmpty();
        assertWords();
        assertLongestPrefix("");
    }
}
