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

    public static <T> String getLongestPrefixFromPrefixAndFilter(ReadOnlyTrie<T> trie,
                                                                 String prefix,
                                                                 Predicate<T> filter) {
        final ReadOnlyTrie<T> prefixTrie = trie.subTrie(prefix);
        if (prefixTrie == null) {
            // No such prefix in trie.
            return "";
        }

        // Filter the subTrie.
        final ReadOnlyTrie<T> filteredTrie = prefixTrie.filter(filter);
        return filteredTrie.getLongestPrefix();
    }

    public static <T> WordTrie getWordTrie(ReadOnlyTrie<T> trie,
                                           String prefix) {
        return trie
            .subTrie(prefix)
            .wordTrie();
    }

    public static <T> WordTrie getWordTrieWithFilter(ReadOnlyTrie<T> trie,
                                                     String prefix,
                                                     Predicate<T> filter) {
        return trie
            .subTrie(prefix)
            .filter(filter)
            .wordTrie();
    }

    public static Trie<String> toTrie(List<String> values) {
        final Trie<String> trie = new TrieImpl<>();
        for (String value : values) {
            trie.put(value, value);
        }
        return trie;
    }
}
