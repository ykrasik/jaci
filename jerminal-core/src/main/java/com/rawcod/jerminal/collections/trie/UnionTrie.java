package com.rawcod.jerminal.collections.trie;

import com.google.common.base.Predicate;
import com.rawcod.jerminal.collections.trie.visitor.TrieVisitor;

import java.util.List;

/**
 * User: ykrasik
 * Date: 23/07/2014
 * Time: 22:36
 */
public class UnionTrie<T> implements ReadOnlyTrie<T> {
    private final Trie<T> trie;

    public UnionTrie(ReadOnlyTrie<T> trie1, ReadOnlyTrie<T> trie2) {
        this.primaryTrie = primaryTrie;
        this.secondaryTrie = secondaryTrie;
    }

    private Trie<T> createUnion(TrieImpl<T> trie1, TrieImpl<T> trie2) {
        // Iterate over both trie children, creating UnionNodes for all nodes that are shared among both tries.
        // If both tries contain the same word, this operation will throw an exception,
        // because a word must be associated with a value.

        // Iterate over the trie with the least amount of children,
        // hoping for a quick end to the iteration.
        final TrieImpl<T> primaryTrie;
        final TrieImpl<T> secondaryTrie;
        if (trie1.size() > trie2.size()) {
            primaryTrie = trie2;
            secondaryTrie = trie1;
        } else {
            primaryTrie = trie1;
            secondaryTrie = trie2;
        }

        final TrieImpl<T> unionTrie = new TrieImpl<>();


    }

    @Override
    public int size() {
        return trie.size();
    }

    @Override
    public boolean isEmpty() {
        return trie.isEmpty();
    }

    @Override
    public T get(String word) {
        return trie.get(word);
    }

    @Override
    public List<String> getAllWords() {
        return trie.getAllWords();
    }

    @Override
    public List<String> getAllWordsWithFilter(Predicate<T> filter) {
        return trie.getAllWordsWithFilter(filter);
    }

    @Override
    public List<String> getWordsFromPrefix(String prefix) {
        return trie.getWordsFromPrefix(prefix);
    }

    @Override
    public List<String> getWordsFromPrefixWithFilter(String prefix, Predicate<T> filter) {
        return trie.getWordsFromPrefixWithFilter(prefix, filter);
    }

    @Override
    public List<T> getAllValues() {
        return trie.getAllValues();
    }

    @Override
    public List<T> getAllValuesWithFilter(Predicate<T> filter) {
        return trie.getAllValuesWithFilter(filter);
    }

    @Override
    public List<T> getValuesByPrefix(String prefix) {
        return trie.getValuesByPrefix(prefix);
    }

    @Override
    public List<T> getValuesByPrefixWithFilter(String prefix, Predicate<T> filter) {
        return trie.getValuesByPrefixWithFilter(prefix, filter);
    }

    @Override
    public void visitAllWords(TrieVisitor<T> visitor) {
        trie.visitAllWords(visitor);
    }

    @Override
    public void visitWordsByFilter(TrieVisitor<T> visitor, Predicate<T> filter) {
        trie.visitWordsByFilter(visitor, filter);
    }

    @Override
    public String getLongestPrefix(String prefix) {
        return trie.getLongestPrefix(prefix);
    }

    @Override
    public ReadOnlyTrie<T> union(ReadOnlyTrie<T> other) {
        return trie.union(other);
    }
}
