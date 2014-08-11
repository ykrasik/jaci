package com.rawcod.jerminal.collections.trie;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.node.TrieNode;
import com.rawcod.jerminal.collections.trie.visitor.TrieVisitor;

import java.util.Collection;
import java.util.Map;

/**
 * User: ykrasik
 * Date: 19/07/2014
 * Time: 10:50
 */
public interface Trie<T> {
    boolean isEmpty();
    TrieNode<T> getRoot();

    boolean contains(String word);
    Optional<T> get(String word);

    Collection<String> getWords();
    Collection<T> getValues();
    void visitWords(TrieVisitor<T> visitor);

    String getLongestPrefix();

    Trie<T> subTrie(String prefix);

    <A> Trie<A> map(Function<T, A> function);

    Trie<T> filter(Predicate<T> filter);
    Trie<T> union(Trie<T> other);

    Map<String, T> toMap();
}
