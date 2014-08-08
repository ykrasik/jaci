package com.rawcod.jerminal.collections.trie;

import com.google.common.base.Optional;
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
    List<T> getAllValues();
    void visitAllWords(TrieVisitor<T> visitor);

    String getLongestPrefix();

    Optional<ReadOnlyTrie<T>> subTrie(String prefix);
    Optional<ReadOnlyTrie<T>> filter(Predicate<T> filter);

    TrieView trieView();
}
