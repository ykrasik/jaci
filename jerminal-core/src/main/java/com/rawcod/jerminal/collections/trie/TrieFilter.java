package com.rawcod.jerminal.collections.trie;

/**
 * User: ykrasik
 * Date: 11/01/14
 */
public interface TrieFilter<T> {
    boolean shouldKeep(T value);

    class NoTrieFilter<T> implements TrieFilter<T> {
        @Override
        public boolean shouldKeep(T value) {
            return true;
        }
    }
}
