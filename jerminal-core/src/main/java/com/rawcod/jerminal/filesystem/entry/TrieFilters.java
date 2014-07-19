package com.rawcod.jerminal.filesystem.entry;

import com.rawcod.jerminal.collections.trie.TrieFilter;

/**
 * User: ykrasik
 * Date: 18/07/2014
 * Time: 23:37
 */
@SuppressWarnings("unchecked")
public final class TrieFilters {
    private TrieFilters() {

    }

    public static final TrieFilter<ShellEntry> NO_FILTER = new TrieFilter.NoTrieFilter();

    public static final TrieFilter<ShellEntry> FILE_FILTER = new TrieFilter<ShellEntry>() {
        @Override
        public boolean shouldFilter(ShellEntry value) {
            return value.isDirectory();
        }
    };

    public static final TrieFilter<ShellEntry> DIRECTORY_FILTER = new TrieFilter<ShellEntry>() {
        @Override
        public boolean shouldFilter(ShellEntry value) {
            return !value.isDirectory();
        }
    };
}
