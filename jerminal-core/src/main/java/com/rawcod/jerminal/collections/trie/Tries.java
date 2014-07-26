package com.rawcod.jerminal.collections.trie;

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
    private Tries() {
    }

    public static <T> List<String> getWordsFromPrefix(ReadOnlyTrie<T> trie, String prefix) {
        return getWordsFromPrefixWithFilter(trie, prefix, Predicates.<T>alwaysTrue());
    }

    public static <T> List<String> getWordsFromPrefixWithFilter(ReadOnlyTrie<T> trie,
                                                                String prefix,
                                                                Predicate<T> filter) {
        final ReadOnlyTrie<T> prefixTrie = trie.subTrie(prefix);
        if (prefixTrie == null) {
            // No such prefix in trie.
            return Collections.emptyList();
        }
        return prefixTrie.getWordsWithFilter(filter);
    }

    public static <T> List<T> getValuesByPrefix(ReadOnlyTrie<T> trie, String prefix) {
        return getValuesByPrefixWithFilter(trie, prefix, Predicates.<T>alwaysTrue());
    }

    public static <T> List<T> getValuesByPrefixWithFilter(ReadOnlyTrie<T> trie,
                                                          String prefix,
                                                          Predicate<T> filter) {
        final ReadOnlyTrie<T> prefixTrie = trie.subTrie(prefix);
        if (prefixTrie == null) {
            // No such prefix in trie.
            return Collections.emptyList();
        }
        return prefixTrie.getValuesWithFilter(filter);
    }
}
