package com.github.ykrasik.jerminal.collections.trie;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.junit.Before;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: ykrasik
 * Date: 11/08/2014
 * Time: 21:37
 */
public class AbstractTrieTest {
    private Map<String, String> valueMap;
    private TrieBuilder<String> trieBuilder;
    protected Trie<String> trie;

    @Before
    public void setUp() {
        this.valueMap = new HashMap<>();
        this.trieBuilder = new TrieBuilder<>();
        this.trie = null;
    }

    protected void build() {
        trie = trieBuilder.build();
    }

    protected void addWord(String word, String value) {
        valueMap.put(word, value);
        trieBuilder.add(word, value);
    }

    protected void successfulSubTrie(String prefix) {
        trie = trie.subTrie(prefix);
        assertFalse("No subTrie for prefix: " + prefix, trie.isEmpty());
    }

    protected void failedSubTrie(String prefix) {
        assertTrue("Unexpected subTrie for prefix: " + prefix, trie.subTrie(prefix).isEmpty());
    }

    protected void assertEmpty() {
        assertTrue("Trie isn't empty!", trie.isEmpty());
    }

    protected void assertNotEmpty() {
        assertFalse("Trie is empty!", trie.isEmpty());
    }

    protected void assertWords(String... expectedWords) {
        final Map<String, String> expectedValueMap = Maps.toMap(Arrays.asList(expectedWords), new Function<String, String>() {
            @Override
            public String apply(String input) {
                return valueMap.get(input);
            }
        });

        assertEquals(expectedValueMap, trie.toMap());
//        final Set<String> expectedWordsSet = Sets.newHashSet(expectedWords);
//        assertEquals("Words mismatch!", expectedWordsSet, new HashSet<>(trie.getWords()));
//
//        final Map<String, String> expectedValueMap = Maps.filterKeys(this.valueMap, new Predicate<String>() {
//            @Override
//            public boolean apply(String input) {
//                return expectedWordsSet.contains(input);
//            }
//        });
//        final Set<String> expectedValues = new HashSet<>(expectedValueMap.values());
//        assertEquals("Values mismatch!", expectedValues, new HashSet<>(trie.getValues()));
    }

    protected void assertLongestPrefix(String expectedPrefix) {
        assertEquals("Invalid longest prefix!", expectedPrefix, trie.getLongestPrefix());
    }
}
