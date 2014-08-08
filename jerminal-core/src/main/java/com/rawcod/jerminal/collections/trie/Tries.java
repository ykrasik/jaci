package com.rawcod.jerminal.collections.trie;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import java.util.Collections;
import java.util.List;

/**
 * User: ykrasik
 * Date: 26/07/2014
 * Time: 14:57
 */
public final class Tries {
    private static final TrieImpl<Object> EMPTY_TRIE = new TrieImpl<>();

    private Tries() {
    }

    @SuppressWarnings("unchecked")
    public static <T> ReadOnlyTrie<T> emptyTrie() {
        return (ReadOnlyTrie<T>) EMPTY_TRIE;
    }

    public static <T> List<String> getWordsFromPrefix(ReadOnlyTrie<T> trie, String prefix) {
        return getWordsFromPrefixWithFilter(trie, prefix, Predicates.<T>alwaysTrue());
    }

    public static <T> List<String> getWordsFromPrefixWithFilter(ReadOnlyTrie<T> trie,
                                                                String prefix,
                                                                Predicate<T> filter) {
        final Optional<ReadOnlyTrie<T>> prefixTrie = trie.subTrie(prefix);
        if (!prefixTrie.isPresent()) {
            // No such prefix in trie.
            return Collections.emptyList();
        }
        final Optional<ReadOnlyTrie<T>> filteredTrie = prefixTrie.get().filter(filter);
        if (!filteredTrie.isPresent()) {
            return Collections.emptyList();
        }
        return filteredTrie.get().getAllWords();
    }

    public static <T> List<T> getValuesByPrefix(ReadOnlyTrie<T> trie, String prefix) {
        return getValuesByPrefixWithFilter(trie, prefix, Predicates.<T>alwaysTrue());
    }

    public static <T> List<T> getValuesByPrefixWithFilter(ReadOnlyTrie<T> trie,
                                                          String prefix,
                                                          Predicate<T> filter) {
        final Optional<ReadOnlyTrie<T>> prefixTrie = trie.subTrie(prefix);
        if (!prefixTrie.isPresent()) {
            // No such prefix in trie.
            return Collections.emptyList();
        }
        final Optional<ReadOnlyTrie<T>> filteredTrie = prefixTrie.get().filter(filter);
        if (!filteredTrie.isPresent()) {
            return Collections.emptyList();
        }
        return filteredTrie.get().getAllValues();
    }

    public static <T> Optional<TrieView> getTrieView(ReadOnlyTrie<T> trie, String prefix) {
        final Optional<ReadOnlyTrie<T>> prefixTrie = trie.subTrie(prefix);
        if (!prefixTrie.isPresent()) {
            // No such prefix in trie.
            return Optional.absent();
        }

        return Optional.of(prefixTrie.get().trieView());
    }

    public static <T> Optional<TrieView> getTrieViewWithFilter(ReadOnlyTrie<T> trie,
                                                               String prefix,
                                                               Predicate<T> filter) {
        final Optional<ReadOnlyTrie<T>> prefixTrie = trie.subTrie(prefix);
        if (!prefixTrie.isPresent()) {
            // No such prefix in trie.
            return Optional.absent();
        }
        final Optional<ReadOnlyTrie<T>> filteredTrie = prefixTrie.get().filter(filter);
        if (!filteredTrie.isPresent()) {
            return Optional.absent();
        }
        return Optional.of(filteredTrie.get().trieView());
    }
}
