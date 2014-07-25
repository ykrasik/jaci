package com.rawcod.jerminal.collections.trie;

import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.visitor.TrieVisitor;

import java.util.List;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 10:50
 */
public interface ReadOnlyTrie<T> {
    int size();
    boolean isEmpty();

    boolean contains(String word);
    T get(String word);

    List<String> getAllWords();
    List<String> getAllWordsWithFilter(Predicate<T> filter);

    List<String> getWordsFromPrefix(String prefix);
    List<String> getWordsFromPrefixWithFilter(String prefix, Predicate<T> filter);

    List<T> getAllValues();
    List<T> getAllValuesWithFilter(Predicate<T> filter);

    List<T> getValuesByPrefix(String prefix);
    List<T> getValuesByPrefixWithFilter(String prefix, Predicate<T> filter);

    void visitAllWords(TrieVisitor<T> visitor);
    void visitWordsByFilter(TrieVisitor<T> visitor, Predicate<T> filter);

    String getLongestPrefix(String prefix);
    String getLongestPrefixWithFilter(String prefix, Predicate<T> filter);

    ReadOnlyTrie<T> union(ReadOnlyTrie<T> other);
}
