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
    boolean isEmpty();

    boolean contains(String word);
    T get(String word);

    List<String> getAllWords();
    List<String> getWordsWithFilter(Predicate<T> filter);

    List<T> getAllValues();
    List<T> getValuesWithFilter(Predicate<T> filter);

    void visitAllWords(TrieVisitor<T> visitor);
    void visitWordsByFilter(TrieVisitor<T> visitor, Predicate<T> filter);

    String getLongestPrefix();

    ReadOnlyTrie<T> subTrie(String prefix);
    ReadOnlyTrie<T> filter(Predicate<T> filter);

    TrieView trieView();
}
