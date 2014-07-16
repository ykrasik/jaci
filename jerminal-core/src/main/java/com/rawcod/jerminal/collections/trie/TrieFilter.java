package com.rawcod.jerminal.collections.trie;

/**
 * User: ykrasik
 * Date: 11/01/14
 */
public interface TrieFilter<T> {
    boolean shouldFilter(T value);

    class NoTrieFilter<T> implements TrieFilter<T> {
        @Override
        public boolean shouldFilter(T value) {
            return false;
        }
    }
}
