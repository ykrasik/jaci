package com.rawcod.jerminal.collections.trie;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.visitor.TrieVisitor;

import java.util.List;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 10:50
 */
public interface Trie<T> {
    boolean isEmpty();

    boolean contains(String word);
    Optional<T> get(String word);

    List<String> getWords();
    List<T> getValues();
    void visitAllWords(TrieVisitor<T> visitor);

    String getLongestPrefix();

    Trie<T> subTrie(String prefix);

    <A> Trie<A> map(Function<T, A> function);
    Trie<T> filter(Predicate<T> filter);

    Trie<List<T>> union(Trie<T> other);
}
