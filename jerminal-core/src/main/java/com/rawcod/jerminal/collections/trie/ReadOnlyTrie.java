package com.rawcod.jerminal.collections.trie;

import java.util.List;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 10:50
 */
public interface ReadOnlyTrie<V> {
    boolean isEmpty();

    V get(String word);

    List<String> getWords(String prefix);
    List<String> getWordsByFilter(String prefix, TrieFilter<V> filter);

    List<String> getAllWords();
    List<String> getAllWordsByFilter(TrieFilter<V> filter);

    List<V> getValues(String prefix);
    List<V> getValuesByFilter(String prefix, TrieFilter<V> filter);

    List<V> getAllValues();
    List<V> getAllValuesByFilter(TrieFilter<V> filter);

    String getLongestPrefix(String prefix);
    String getLongestExistingPrefix(String word);

    void visitAllWords(TrieVisitor<V> visitor);
    void visitWordsByFilter(TrieVisitor<V> visitor, TrieFilter<V> filter);
}
